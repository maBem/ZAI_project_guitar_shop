package com.example.maciekshop.controller;

import java.util.List;

import com.example.maciekshop.dao.AccountDAO;
import com.example.maciekshop.entity.Account;
import com.example.maciekshop.form.SignUpForm;
import com.example.maciekshop.validator.SignUpFormValidator;
import org.apache.commons.lang.exception.ExceptionUtils;
import com.example.maciekshop.dao.OrderDAO;
import com.example.maciekshop.dao.ProductDAO;
import com.example.maciekshop.entity.Product;
import com.example.maciekshop.form.ProductForm;
import com.example.maciekshop.model.OrderDetailInfo;
import com.example.maciekshop.model.OrderInfo;
import com.example.maciekshop.pagination.PaginationResult;
import com.example.maciekshop.validator.ProductFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Transactional
public class AdminController {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private ProductFormValidator productFormValidator;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private SignUpFormValidator signUpFormValidator;

    @InitBinder
    public void myInitBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        System.out.println("Target=" + target);

        if (target.getClass() == ProductForm.class) {
            dataBinder.setValidator(productFormValidator);
        }
        if(target.getClass() == SignUpForm.class){
            dataBinder.setValidator(signUpFormValidator);
        }
    }



    @RequestMapping(value = { "/admin/accountInfo" }, method = RequestMethod.GET)
    public String accountInfo(Model model) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(userDetails.getPassword());
        System.out.println(userDetails.getUsername());
        System.out.println(userDetails.isEnabled());

        model.addAttribute("userDetails", userDetails);
        return "accountInfo";
    }

    @RequestMapping(value = { "/admin/orderList" }, method = RequestMethod.GET)
    public String orderList(Model model, //
                            @RequestParam(value = "page", defaultValue = "1") String pageStr) {
        int page = 1;
        try {
            page = Integer.parseInt(pageStr);
        } catch (Exception e) {
        }
        final int MAX_RESULT = 20;
        final int MAX_NAVIGATION_PAGE = 10;

        PaginationResult<OrderInfo> paginationResult //
                = orderDAO.listOrderInfo(page, MAX_RESULT, MAX_NAVIGATION_PAGE);

        model.addAttribute("paginationResult", paginationResult);
        return "orderList";
    }

    @RequestMapping(value = { "/admin/product" }, method = RequestMethod.GET)
    public String product(Model model, @RequestParam(value = "code", defaultValue = "") String code) {
        ProductForm productForm = null;

        if (code != null && code.length() > 0) {
            Product product = productDAO.findProduct(code);
            if (product != null) {
                productForm = new ProductForm(product);
            }
        }
        if (productForm == null) {
            productForm = new ProductForm();
            productForm.setNewProduct(true);
        }
        model.addAttribute("productForm", productForm);
        return "product";
    }

    @RequestMapping(value = { "/admin/product" }, method = RequestMethod.POST)
    public String productSave(Model model, //
                              @ModelAttribute("productForm") @Validated ProductForm productForm, //
                              BindingResult result, //
                              final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "product";
        }
        try {
            productDAO.save(productForm);
        } catch (Exception e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            String message = rootCause.getMessage();
            model.addAttribute("errorMessage", message);
            // Show product form.
            return "product";
        }

        return "redirect:/productList";
    }

    @RequestMapping(value = { "/admin/signup" }, method = RequestMethod.GET)
    public String signUpFormMethod(Model model, @RequestParam(value = "userName", defaultValue = "") String userName) {

        SignUpForm signUpForm = null;

        if(userName != null && userName.length()>0){
            Account account = accountDAO.findAccount(userName);
            if (account != null) {
                signUpForm = new SignUpForm(account);
            }
        }
        if (signUpForm == null) {
            signUpForm= new SignUpForm();
            signUpForm.setNewAccount(true);
        }

        model.addAttribute("signUpForm", signUpForm);
        return "signup";
    }

    @RequestMapping(value = { "/admin/signup" }, method = RequestMethod.POST)
    public String signUpSave(Model model, @ModelAttribute("signUpForm") @Validated SignUpForm signUpForm, //
                             BindingResult result, //
                             final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            signUpForm.setValid(false);
            return "signup";
        }
        try {
            accountDAO.save(signUpForm);
        } catch (Exception e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            String message = rootCause.getMessage();
            model.addAttribute("errorMessage", message);
            return "signup";
        }
        signUpForm.setValid(true);

        return "redirect:/admin/login";
    }

    @RequestMapping(value = { "/admin/login" }, method = RequestMethod.GET)
    public String login(Model model) {

        return "login";
    }

    @RequestMapping(value = { "/admin/order" }, method = RequestMethod.GET)
    public String orderView(Model model, @RequestParam("orderId") String orderId) {
        OrderInfo orderInfo = null;
        if (orderId != null) {
            orderInfo = this.orderDAO.getOrderInfo(orderId);
        }
        if (orderInfo == null) {
            return "redirect:/admin/orderList";
        }
        List<OrderDetailInfo> details = this.orderDAO.listOrderDetailInfos(orderId);
        orderInfo.setDetails(details);

        model.addAttribute("orderInfo", orderInfo);

        return "order";
    }

}