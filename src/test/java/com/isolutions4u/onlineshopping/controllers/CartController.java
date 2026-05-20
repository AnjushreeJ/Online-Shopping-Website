package com.isolutions4u.onlineshopping.controllers;

import com.isolutions4u.onlineshopping.model.Cart;
import com.isolutions4u.onlineshopping.model.CartLine;
import com.isolutions4u.onlineshopping.model.Product;
import com.isolutions4u.onlineshopping.model.User;
import com.isolutions4u.onlineshopping.service.CartLineService;
import com.isolutions4u.onlineshopping.service.CartService;
import com.isolutions4u.onlineshopping.service.ProductService;
import com.isolutions4u.onlineshopping.service.SmsService;
import com.isolutions4u.onlineshopping.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    @Qualifier("cartLineService")
    private CartLineService cartLineService;

    @Autowired
    @Qualifier("productService")
    private ProductService productService;

    @Autowired
    @Qualifier("cartService")
    private CartService cartService;

    @Autowired
    private SmsService smsService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    // SHOW CART
    @GetMapping("/show")
    public ModelAndView showCart(
            @RequestParam(name = "result", required = false) String result) {

        ModelAndView mv = new ModelAndView("page");

        if (result != null) {

            switch (result) {

                case "updated":
                    mv.addObject("message",
                            "Cart item updated successfully.");
                    break;

                case "added":
                    mv.addObject("message",
                            "Product added to cart successfully!");
                    break;

                case "alreadyAdded":
                    mv.addObject("message",
                            "Product is already in your cart.");
                    break;

                case "deleted":
                    mv.addObject("message",
                            "Item removed from cart successfully.");
                    break;

                case "outOfStock":
                    mv.addObject("message",
                            "Sorry, this product is out of stock.");
                    break;

                case "notFound":
                    mv.addObject("message",
                            "Product not found.");
                    break;

                case "error":
                    mv.addObject("message",
                            "Something went wrong.");
                    break;

                default:
                    break;
            }
        }

        mv.addObject("title", "My Cart");

        mv.addObject("userClickShowCart", true);

        mv.addObject("cartLines",
                cartLineService.findCartLines());

        Cart cart = cartService.findCart();

        if (cart != null) {

            mv.addObject("grandTotal",
                    cart.getGrandTotal());

            mv.addObject("totalItems",
                    cart.getCartLines());
        }

        return mv;
    }

    // CHECKOUT PAGE
    @GetMapping("/validate")
    public ModelAndView validateCart() {

        ModelAndView mv = new ModelAndView("page");

        try {

            Cart cart = cartService.findCart();

            if (cart == null || cart.getCartLines() < 1) {

                mv.addObject("title", "My Cart");

                mv.addObject("userClickShowCart", true);

                mv.addObject("message",
                        "Your cart is empty!");

                mv.addObject("cartLines",
                        cartLineService.findCartLines());

                return mv;
            }

            mv.addObject("title", "Checkout");

            mv.addObject("userClickCheckout", true);

            mv.addObject("cartLines",
                    cartLineService.findCartLines());

            mv.addObject("grandTotal",
                    cart.getGrandTotal());

            mv.addObject("totalItems",
                    cart.getCartLines());

        } catch (Exception e) {

            e.printStackTrace();

            mv.addObject("title", "My Cart");

            mv.addObject("userClickShowCart", true);

            mv.addObject("message",
                    "Error : " + e.getMessage());
        }

        return mv;
    }

    // ADD PRODUCT TO CART
    @GetMapping("/add/{id}/product")
    public String addCart(@PathVariable int id) {

        Product product = productService.findProductById(id);

        if (product == null) {
            return "redirect:/cart/show?result=notFound";
        }

        if (product.getQuantity() < 1) {
            return "redirect:/cart/show?result=outOfStock";
        }

        Cart cart = cartService.findCart();

        CartLine existingLine =
                cartLineService.findCartLineByCartIdAndProductId(
                        cart.getId(), id);

        if (existingLine != null) {
            return "redirect:/cart/show?result=alreadyAdded";
        }

        CartLine cartLine = new CartLine();

        cartLine.setCartId(cart.getId());

        cartLine.setProduct(product);

        cartLine.setBuyingPrice(product.getUnitPrice());

        cartLine.setProductCount(1);

        cartLine.setTotal(product.getUnitPrice());

        cartLine.setAvailable(true);

        cartLineService.saveCartLine(cartLine);

        cart.setCartLines(cart.getCartLines() + 1);

        cart.setGrandTotal(
                cart.getGrandTotal() + cartLine.getTotal());

        cartService.saveCart(cart);

        return "redirect:/cart/show?result=added";
    }

    // UPDATE CART
    @GetMapping("/{id}/update")
    public String updateCart(
            @PathVariable int id,
            @RequestParam int count) {

        CartLine cartLine =
                cartLineService.findCartLineById(id);

        if (cartLine == null) {
            return "redirect:/cart/show?result=error";
        }

        Product product = cartLine.getProduct();

        if (count < 1)
            count = 1;

        if (count > product.getQuantity())
            count = product.getQuantity();

        double oldTotal = cartLine.getTotal();

        cartLine.setProductCount(count);

        cartLine.setBuyingPrice(product.getUnitPrice());

        cartLine.setTotal(
                product.getUnitPrice() * count);

        cartLineService.updateCartLine(cartLine);

        Cart cart = cartService.findCart();

        cart.setGrandTotal(
                cart.getGrandTotal()
                        - oldTotal
                        + cartLine.getTotal());

        cartService.updateCart(cart);

        return "redirect:/cart/show?result=updated";
    }

    // PLACE ORDER
    @GetMapping("/placeorder")
    public ModelAndView placeOrder() {

        ModelAndView mv = new ModelAndView("page");

        try {

            Cart cart = cartService.findCart();

            if (cart == null || cart.getCartLines() < 1) {

                mv.addObject("title", "My Cart");

                mv.addObject("userClickShowCart", true);

                mv.addObject("message",
                        "Your cart is empty.");

                mv.addObject("cartLines",
                        cartLineService.findCartLines());

                return mv;
            }

            // Logged in user
            String email =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getName();

            User user =
                    userService.findUserByEmail(email);
            
            // FIRST SHOW SUCCESS PAGE
            mv.addObject("title", "Order Success");

            mv.addObject(
                    "userClickOrderSuccess",
                    true);

            mv.addObject(
                    "grandTotal",
                    cart.getGrandTotal());

            mv.addObject(
                    "totalItems",
                    cart.getCartLines());

            // SMS Console Demo
            System.out.println("=================================");

            System.out.println("ORDER CONFIRMED");

            System.out.println(
                    "Thank You For Shopping!");

            System.out.println(
                    "Customer : "
                            + user.getFirstName()
                            + " "
                            + user.getLastName());

            System.out.println(
                    "Total Amount : "
                            + cart.getGrandTotal());

            System.out.println("=================================");

//            // Send SMS
//            smsService.sendOrderConfirmation(
//                    user.getFirstName()
//                            + " "
//                            + user.getLastName(),
//                    cart.getCartLines(),
//                    cart.getGrandTotal()
//            );

            // Store totals
            double finalTotal =
                    cart.getGrandTotal();

            int totalItems =
                    cart.getCartLines();

            // Delete cart lines
//            for (CartLine line :
//                    cartLineService.findCartLines()) {
//
//                cartLineService.deleteCartLine(line);
//            }
//
//            // Clear cart
//            cart.setGrandTotal(0);
//
//            cart.setCartLines(0);
//
//            cartService.updateCart(cart);

//            // Success page
//            mv.addObject("title",
//                    "Order Success");
//
//            mv.addObject(
//                    "userClickOrderSuccess",
//                    true);
//
//            mv.addObject(
//                    "grandTotal",
//                    finalTotal);
//
//            mv.addObject(
//                    "totalItems",
//                    totalItems);

        } catch (Exception e) {

            e.printStackTrace();

            mv.addObject("title",
                    "Checkout");

            mv.addObject(
                    "userClickCheckout",
                    true);

            mv.addObject(
                    "message",
                    "Something went wrong : "
                            + e.getMessage());

            mv.addObject(
                    "cartLines",
                    cartLineService.findCartLines());
        }

        return mv;
    }

    // DELETE CART ITEM
    @GetMapping("/{id}/delete")
    public String deleteCart(@PathVariable int id) {

        CartLine cartLine =
                cartLineService.findCartLineById(id);

        if (cartLine == null) {
            return "redirect:/cart/show?result=error";
        }

        Cart cart = cartService.findCart();

        cart.setGrandTotal(
                cart.getGrandTotal()
                        - cartLine.getTotal());

        cart.setCartLines(
                cart.getCartLines() - 1);

        cartService.updateCart(cart);

        cartLineService.deleteCartLine(cartLine);

        return "redirect:/cart/show?result=deleted";
    }
}