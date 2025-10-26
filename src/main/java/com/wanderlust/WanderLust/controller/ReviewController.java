package com.wanderlust.WanderLust.controller;

import com.wanderlust.WanderLust.entity.ReviewEntity;
import com.wanderlust.WanderLust.entity.UserEntity;
import com.wanderlust.WanderLust.repo.ReviewRepo;
import com.wanderlust.WanderLust.repo.UserRepo;
import com.wanderlust.WanderLust.service.ListingService;
import com.wanderlust.WanderLust.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ListingService listingService;
    private final ReviewService reviewService;
    private final ReviewRepo reviewRepo;
    private final UserRepo userRepo;

    @PostMapping("/add/{listingId}")
    public String addReview(@PathVariable Long listingId,
                            @Valid @ModelAttribute("newReview") ReviewEntity review,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes,Model model,
                            HttpSession session) {
        Long loggedUserId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (loggedUserId == null) {
            return "redirect:/auth/login?redirect=/reviews/" + listingId + "/add";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("selectedList", listingService.findById(listingId));
            redirectAttributes.addFlashAttribute("failure","Reviews Not Added Please Try Again We Appreciate Your Efforts");
            return "listings-detail";
        }

        UserEntity user = userRepo.findById(loggedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        review.setUser(user);

        // Also set the listing
        review.setListing(listingService.findById(listingId));
        reviewService.saveReview(listingId, review);
        redirectAttributes.addFlashAttribute("success", " Review added successfully! Thank you 🙏");
        return "redirect:/listings/"+listingId;
    }

    @PostMapping("/{listingId}/delete/{reviewId}")
    public String deleteReview(@PathVariable Long listingId,
                               HttpSession session,
                               @PathVariable Long reviewId, RedirectAttributes redirectAttributes) {
        Long loggedUserId=(Long) session.getAttribute("LOGGED_USER_ID");
        if(loggedUserId==null)return "redirect:/auth/login?redirect=/reviews/"+listingId+"/delete"+reviewId;
        ReviewEntity review=reviewRepo.findById(reviewId).orElseThrow(()-> new RuntimeException("Review Not Found"));
        if(!review.getUser().getId().equals(loggedUserId)){
            redirectAttributes.addFlashAttribute("failure", "You can only delete your own reviews!");
            return "redirect:/listings/" + review.getListing().getId();
        }
        reviewService.deleteReview(reviewId);
        redirectAttributes.addFlashAttribute("success","Review Deleted SuccessFully Please Give Us Review");
        return "redirect:/listings/" + listingId;
    }
}
