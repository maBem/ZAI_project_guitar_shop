package com.example.maciekshop.form;

import com.example.maciekshop.entity.Account;
import com.example.maciekshop.entity.Product;
import org.springframework.web.multipart.MultipartFile;

public class SignUpForm {

    private String userName;
    private String password;
    private String email;

    private boolean isNewAccount = false;
    private boolean valid;



    public SignUpForm()
    {
        this.isNewAccount = true;
    }
    public SignUpForm(Account account) {
        this.userName = account.getUserName();
        this.password = account.getEncrytedPassword();
        this.email = account.getEmail();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isNewAccount() {
        return isNewAccount;
    }

    public void setNewAccount(boolean newAccount) {
        isNewAccount = newAccount;
    }
}
