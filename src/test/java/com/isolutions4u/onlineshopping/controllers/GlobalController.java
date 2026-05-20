package com.isolutions4u.onlineshopping.controllers;

import com.isolutions4u.onlineshopping.model.User;
import com.isolutions4u.onlineshopping.model.UserModel;
import com.isolutions4u.onlineshopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserService userService;

    @ModelAttribute("userModel")
    public UserModel getUserModel() {

        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();

        // Skip for anonymous users (not logged in)
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return null;
        }

        // ── Always fetch fresh from DB so cart totals are always current ──
        User user = userService.findUserByEmail(authentication.getName());

        if (user == null) {
            return null;
        }

        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setEmail(user.getEmail());
        userModel.setRole(user.getRole());
        userModel.setFullName(user.getFirstName() + " " + user.getLastName());

        // Set cart only for USER role
        if (user.getRole() != null
                && user.getRole().equalsIgnoreCase("USER")) {
            userModel.setCart(user.getCart());
        }

        // Update session with fresh data
        httpSession.setAttribute("userModel", userModel);

        return userModel;
    }
}