//package com.wanderlust.WanderLust.controller;
//
//import com.wanderlust.WanderLust.dto.LoginRequest;
//import com.wanderlust.WanderLust.entity.UserEntity;
//import com.wanderlust.WanderLust.error.ApiError;
//import com.wanderlust.WanderLust.repo.UserRepo;
//import com.wanderlust.WanderLust.security.JwtService;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.util.http.parser.Cookie;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//
//@Controller
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//@Slf4j
//public class AuthController {
//
//    private final UserRepo userRepo;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//
//    private final AuthenticationManager authenticationManager;
//
////    @GetMapping("/signup")
////    public String signup(@RequestParam(required = false)String redirect, Model model, HttpSession session){
////        Object jwt=session.getAttribute("JWT_TOKEN");
////        if(jwt!=null){
////            if(redirect!=null && !redirect.isEmpty()){
////                return "redirect:"+redirect;
////            }
////            return "redirect:/listings/all";
////        }
////        model.addAttribute("user",new UserEntity());
////        return "auth/signup";
////    }
////
////    @PostMapping("/signup/user")
////    public String signupUser(@ModelAttribute("user") UserEntity user,@RequestParam(required = false)String redirect){
////
////        if(userRepo.findByEmail(user.getEmail()).orElse(null)!=null){
////            return "error";
////        }
////        user=UserEntity.builder()
////                .email(user.getEmail())
////                .name(user.getName())
////                .password(passwordEncoder.encode(user.getPassword()))
////                .build();
////
////        userRepo.save(user);
////        return "redirect:/auth/login";
////    }
////
////    @GetMapping("/login")
////    public String login(Model model){
////        model.addAttribute("login",new LoginRequest());
////        return "auth/login";
////    }
////
////    @PostMapping("/login")
////    public String login(@ModelAttribute("login") LoginRequest loginRequest, HttpSession session,Model model){
////        try{
////            Authentication authentication=authenticationManager.authenticate(
////                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword())
////            );
////
////            UserDetails userDetails=(UserDetails) authentication.getPrincipal();
////            UserEntity user=userRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
////            System.out.println(user);
////            String token=jwtService.generateToken(user);
////            System.out.println(token);
////            session.setAttribute("JWT_TOKEN",token);
////            session.setAttribute("LOGGED_USER_ID", user.getId());
////            return "redirect:/listings/all";
////        }catch (Exception e){
////            log.error("Authentication failed: {}", e.getMessage(), e);
////            ApiError apiError=new ApiError("Something Wrong try Again"+e.getMessage(), HttpStatus.BAD_REQUEST);
////            model.addAttribute("error",apiError);
////            return "error";
////        }
////    }
//
//
//}
package com.wanderlust.WanderLust.controller;

import com.wanderlust.WanderLust.dto.LoginRequest;
import com.wanderlust.WanderLust.dto.LoginResponseDto;
import com.wanderlust.WanderLust.entity.UserEntity;
import com.wanderlust.WanderLust.error.ApiError;
import com.wanderlust.WanderLust.repo.UserRepo;
import com.wanderlust.WanderLust.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Show Signup Page
    @GetMapping("/signup")
    public String signupPage(@RequestParam(required = false) String redirect, HttpSession session, Model model) {
        if (session.getAttribute("JWT_TOKEN") != null) {
            // Already logged in
            return "redirect:/listings/all";
        }
        model.addAttribute("redirect", redirect);
        model.addAttribute("user", new UserEntity());
        return "auth/signup";
    }

    // Handle Signup
    @PostMapping("/signup/user")
    public String signupUser(@ModelAttribute("user") UserEntity user,
                             @RequestParam(required = false) String redirect,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,
                             HttpServletResponse response) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return "redirect:/auth/login?error=email_exists";
        }

        String rawPassword = user.getPassword(); // save before encoding

        user = UserEntity.builder()
                .email(user.getEmail())
                .name(user.getName())
                .password(passwordEncoder.encode(user.getPassword()))
                .build();

        userRepo.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), rawPassword)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token=jwtService.generateToken(user);
        session.setAttribute("JWT_TOKEN",token);
        session.setAttribute("LOGGED_USER_ID",user.getId());
        Cookie cookie=new Cookie("wanderlust",token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(100 * 60*60);
        response.addCookie(cookie);

        if (redirect != null && !redirect.isEmpty()) {
            return "redirect:" + redirect;
        }

        // Redirect after signup
        redirectAttributes.addFlashAttribute("success", "Signup Successful — Welcome to Wanderlust!");
        return "redirect:/listings/all";
    }

    // Show Login Page
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String redirect, HttpSession session, Model model) {
        if (session.getAttribute("JWT_TOKEN") != null) {
            // Already logged in
            return "redirect:/listings/all";
        }
        model.addAttribute("redirect", redirect);
        log.info("Login page loaded with redirect={}", redirect);
        model.addAttribute("login", new LoginRequest());
        return "auth/login";
    }

//    Handle Login

    @PostMapping("/login")
    public String login(@ModelAttribute("login") LoginRequest loginRequest,
                        @RequestParam(required = false) String redirect,
                        HttpSession session,
                        HttpServletResponse response,
                        HttpServletRequest request,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()) );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal(); UserEntity user = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            //  Generate JWT
            String token = jwtService.generateToken(user);
            session.setAttribute("JWT_TOKEN", token);
            session.setAttribute("LOGGED_USER_ID", user.getId());
            //  Save cookie
            Cookie cookie = new Cookie("wanderlust", token);
            cookie.setHttpOnly(true); cookie.setPath("/");
            cookie.setMaxAge(100 * 60*60);
            response.addCookie(cookie);
            log.info("Redirect after login: {}", redirect);
            // Redirect back to original page (if present)
            if (redirect != null && !redirect.isEmpty()) {
                log.info("Redirecting to: {}", redirect);
                return "redirect:" + redirect;
            }
            redirectAttributes.addFlashAttribute("success","Welcome Back to Wanderlust");
            return "redirect:/listings/all";
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            ApiError apiError = new ApiError("Invalid credentials",
                    HttpStatus.BAD_REQUEST); model.addAttribute("error", apiError);
                    return "error";
        }
    }
    //  Logout (clear session and cookie)
//     @GetMapping("/logout")
//     public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
//         HttpSession session = request.getSession(false);
//         if (session != null) {
//             session.removeAttribute("JWT_TOKEN");
//             session.removeAttribute("LOGGED_USER_ID");
//             session.invalidate();
//         }
//
//         // Delete JWT cookie
//         Cookie jwtCookie = new Cookie("wanderlust", "");
//         jwtCookie.setPath("/");
//         jwtCookie.setHttpOnly(true);
//         jwtCookie.setMaxAge(0);
//         response.addCookie(jwtCookie);
//
//         // Delete SESSION cookie (Spring Session)
//         Cookie sessionCookie = new Cookie("SESSION", "");
//         sessionCookie.setPath("/");
//         sessionCookie.setHttpOnly(true);
//         sessionCookie.setMaxAge(0);
//         response.addCookie(sessionCookie);
//
//         redirectAttributes.addFlashAttribute("success", "Log Out Successful!");
//         return "redirect:/listings/all";
//
//     }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);

        // 🧩 Check and clear expired or invalid JWT
        if (session != null) {
            String jwtToken = (String) session.getAttribute("JWT_TOKEN");

            // If token expired or missing → clear everything and redirect to fresh start
            if (jwtToken == null || jwtService.isTokenExpired(jwtToken)) {
                clearAllCookiesAndSession(session, response);
                session.invalidate();
                redirectAttributes.addFlashAttribute("warning", "Session expired. Starting fresh!");
                return "redirect:/listings/all";
            }

            // Normal logout flow
            session.removeAttribute("JWT_TOKEN");
            session.removeAttribute("LOGGED_USER_ID");
            session.invalidate();
        }

        // Delete cookies
        clearAllCookiesAndSession(null, response);
        redirectAttributes.addFlashAttribute("success", "Log Out Successful!");
        return "redirect:/listings/all";
    }

    /**
     * 🔒 Helper method to clear all cookies and session safely
     */
    private void clearAllCookiesAndSession(HttpSession session, HttpServletResponse response) {
        if (session != null) session.invalidate();

        Cookie jwtCookie = new Cookie("wanderlust", "");
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        Cookie sessionCookie = new Cookie("SESSION", "");
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);
    }


}