package com.wanderlust.WanderLust.controller;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.wanderlust.WanderLust.dto.ListingDto;
import com.wanderlust.WanderLust.entity.ListEntity;
import com.wanderlust.WanderLust.entity.ReviewEntity;
import com.wanderlust.WanderLust.entity.UserEntity;
import com.wanderlust.WanderLust.repo.ListingRepo;
import com.wanderlust.WanderLust.repo.ReviewRepo;
import com.wanderlust.WanderLust.repo.UserRepo;
import com.wanderlust.WanderLust.security.JwtService;
import com.wanderlust.WanderLust.service.ListingService;
import com.wanderlust.WanderLust.service.ReviewService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;
    private final ListingRepo listingRepo;
    private final JwtService jwtService;
//    private static final String UPLOAD_DIR="C:/Users/hi/Downloads/WanderLust/uploads/";
    private final Cloudinary cloudinary;
    private final UserRepo userRepo;

    @GetMapping("/all")
    public String getAllListings(Model model) {
        List<ListEntity> listings = listingRepo.findAll();
        model.addAttribute("listings", listings);
        Object msg=model.getAttribute("message");
        model.addAttribute("message",msg);
        return "listings";
    }


    @GetMapping("/{id}")
    public String getAllList(@PathVariable Long id, Model model,RedirectAttributes redirectAttributes){
        try{
            ListEntity listings=listingService.findByIdWithReviews(id);
            model.addAttribute("selectedList",listings);
            model.addAttribute("newReview", new ReviewEntity());
            return "listings-detail";
        }catch(NoSuchElementException e){
            redirectAttributes.addFlashAttribute("failure","List Was Not Present");
            return "redirect:/listings/all";
        }
    }


    @GetMapping("/new")
    public String showFrom(Model model){
        model.addAttribute("newListing",new ListingDto());
        return "add-listings";
    }


    @PostMapping("/save")
    public String createList(@Valid @ModelAttribute("newListing") ListEntity listing,
                             RedirectAttributes redirectAttributes,
                             BindingResult result,
                             Model model,
                             @CookieValue(value = "wanderlust",required = false) String jwt,
                             HttpSession session,
                             @RequestParam("imageFile")MultipartFile imageFile) throws IOException {

        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("failure", "Validation failed. Please check your input.");
            return "add-listings";
        }
        boolean exists = listingService.existsByTitle(listing.getTitle());
        if (exists) {
            model.addAttribute("failure",
                    "A listing with this title already exists!");
            return "add-listings";
        }

        if (jwt == null) {
            // ✅ No user logged in → store listing in session
            session.setAttribute("pendingListing", listing);
            redirectAttributes.addFlashAttribute("failure", "You need to signup/login first to save the listing.");
            return "redirect:/auth/signup?redirect=/listings/new";
        }

        if(!imageFile.isEmpty()){
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            String imageUrl = uploadResult.get("secure_url").toString();
            listing.setImages_url(imageUrl);
        }


        UserEntity user=jwtService.getUserFromToken(jwt);
        listing.setUser(user);
        // ✅ Save listing successfully
        listingService.saveList(listing);
        redirectAttributes.addFlashAttribute("success",
                "Listing added successfully!");
        return "redirect:/listings/all";
    }


    @GetMapping("/{id}/edit")
    public String editById(@PathVariable Long id,Model model,RedirectAttributes redirectAttributes
            ,HttpSession session){
        String jwtToken=(String)session.getAttribute("JWT_TOKEN");
        Long loggedUserId=(Long) session.getAttribute("LOGGED_USER_ID");
        if(jwtToken==null && loggedUserId==null){
            return "redirect:/auth/login?redirect=/listings/"+id+"/edit";
        }
        try{
            ListEntity listing=listingService.findById(id);
            String originalImgUrl=listing.getImages_url();
            if (originalImgUrl != null && !originalImgUrl.isEmpty()) {
                originalImgUrl = originalImgUrl.replace("/upload", "/upload/h_130,w_250");
            }
            model.addAttribute("originalImgUrl",originalImgUrl);
            model.addAttribute("editedListing",listing);
            return "edit-listing";
        }catch(NoSuchElementException e){
            redirectAttributes.addFlashAttribute("failure","List Was Not Present");
            return "redirect:/listings/all";
        }
    }



    @PostMapping("/{id}/edit/update")
    public String editBYId(@Valid @ModelAttribute ListEntity listing,
                           @PathVariable Long id,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model,
                           HttpSession session,
                           @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        String jwtToken=(String) session.getAttribute("JWT_TOKEN");
        Long loggedUserId=(Long) session.getAttribute("LOGGED_USER_ID");
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("failure", "Validation failed. Please check your input.");
            return "edit-listings";
        }

        if(jwtToken==null || loggedUserId==null){
            return "redirect:/auth/signup?redirect=/listings/"+id+"/edit/update";
        }

        ListEntity list=listingRepo.findById(id).orElseThrow(()->new RuntimeException("Listing Not found"));
        if(!list.getUser().getId().equals(loggedUserId)){
            redirectAttributes.addFlashAttribute("failure", "You are not authorized to delete this listing!");
            return "redirect:/listings/all";
        }
        ListEntity existingListing = listingService.findById(id);
        if(imageFile != null && !imageFile.isEmpty()){
            Map uploadResult=cloudinary.uploader().upload(imageFile.getBytes()
                    ,ObjectUtils.asMap("resource_type","auto"));
            String imageUrl=uploadResult.get("secure_url").toString();
            existingListing.setImages_url(imageUrl);
        }
        existingListing.setTitle(listing.getTitle());
        existingListing.setDescription(listing.getDescription());
        existingListing.setPrice(listing.getPrice());
        existingListing.setLocation(listing.getLocation());
        existingListing.setCountry(listing.getCountry());
        existingListing.setUser(userRepo.findById(loggedUserId).orElseThrow());
        listingService.saveList(existingListing);
        redirectAttributes.addFlashAttribute("success", "✅ Listing updated successfully!");
        return "redirect:/listings/"+listing.getId();

    }


    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable Long id, HttpSession session, Model model,RedirectAttributes redirectAttributes) {
        // Check if user logged in
        String jwtToken=(String) session.getAttribute("JWT_TOKEN");
        Long loggedUserId=(Long) session.getAttribute("LOGGED_USER_ID");

        if(jwtToken==null && loggedUserId==null){
            return "redirect:/auth/login?redirect=/listings/"+id+"/delete";
        }
        ListEntity list=listingRepo.findById(id).orElseThrow(()->new RuntimeException("Listing Not Found"));
        if(!list.getUser().getId().equals(loggedUserId)){
            redirectAttributes.addFlashAttribute("failure", "You are not authorized to delete this listing!");
            return "redirect:/listings/all";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/auth/login?redirect=/listings/" + id + "/delete";
        }

        // Load listing info for confirmation message
        ListEntity listing = listingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        model.addAttribute("listing", listing);
        // Show confirmation page
        return "confirm-delete";
    }

    @PostMapping("/{id}/delete")
    public String deleteById(@PathVariable Long id,RedirectAttributes redirectAttributes){
        listingService.deleteById(id);
        redirectAttributes.addFlashAttribute("success","List Deleted Successfully");
        return "redirect:/listings/all";
    }

}
