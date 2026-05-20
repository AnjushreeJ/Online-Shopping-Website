package com.isolutions4u.onlineshopping.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@ControllerAdvice
public class GlobalDefaultExceptionHandler implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public ModelAndView error(HttpServletRequest request) {

        // Get the status code from the request
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object requestUri = request.getAttribute("javax.servlet.error.request_uri");

        ModelAndView mv = new ModelAndView("404");

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 404) {
                mv.addObject("errorTitle", "Page Not Found!");
                mv.addObject("errorDescription",
                    "The page you are looking for is not available. "
                  + "Requested URL: " + requestUri);

            } else if (statusCode == 403) {
                mv.addObject("errorTitle", "Access Denied!");
                mv.addObject("errorDescription",
                    "You do not have permission to access this page. "
                  + "Please login with the correct account.");

            } else if (statusCode == 500) {
                mv.addObject("errorTitle", "Internal Server Error!");
                mv.addObject("errorDescription",
                    "Something went wrong on our end. Please try again later.");

            } else {
                mv.addObject("errorTitle", "Unexpected Error!");
                mv.addObject("errorDescription",
                    "An unexpected error occurred. Status: " + statusCode);
            }
        } else {
            mv.addObject("errorTitle", "This page is not constructed!");
            mv.addObject("errorDescription",
                "The page you are looking for is not available now!");
        }

        mv.addObject("title", "Error Page");
        return mv;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFound(NoHandlerFoundException ex) {
        ModelAndView mv = new ModelAndView("404");
        mv.addObject("errorTitle", "Page Not Found!");
        mv.addObject("errorDescription",
            "No handler found for " + ex.getHttpMethod()
          + " " + ex.getRequestURL());
        mv.addObject("title", "404 Error Page");
        return mv;
    }

    @ExceptionHandler(ProductNotFoundExceptoion.class)
    public ModelAndView errorProductNotFound() {
        ModelAndView mv = new ModelAndView("404");
        mv.addObject("errorTitle", "Product Not Available");
        mv.addObject("errorDescription",
            "The product you are looking for is not available right now!");
        mv.addObject("title", "Product Unavailable");
        return mv;
    }

    @ExceptionHandler(MultipartException.class)
    public String handleMultipartError(MultipartException e,
                                       RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message",
            e.getCause().getMessage());
        return "redirect:/manage/products";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception ex,
                                               HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("404");
        mv.addObject("errorTitle", "Something Went Wrong!");
        mv.addObject("errorDescription", ex.getMessage());
        mv.addObject("title", "Error");

        // Print to console so you can see it in Eclipse
        System.err.println("=== EXCEPTION CAUGHT ===");
        System.err.println("URL : " + request.getRequestURL());
        System.err.println("Error: " + ex.getMessage());
        ex.printStackTrace();

        return mv;
    }
}