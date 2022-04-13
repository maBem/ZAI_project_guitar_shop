package com.example.maciekshop.dao;

import com.example.maciekshop.entity.Product;
import com.example.maciekshop.form.SignUpForm;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.example.maciekshop.entity.Account;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Transactional
@Repository
public class AccountDAO {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SessionFactory sessionFactory;

    public Account findAccount(String userName) {

        try {
            String sql = "Select e from " + Account.class.getName() + " e Where e.userName =:userName ";

            Session session = this.sessionFactory.getCurrentSession();
            Query<Account> query = session.createQuery(sql, Account.class);
            query.setParameter("userName", userName);
            return (Account) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void save(SignUpForm signUpForm){
        Session session = this.sessionFactory.getCurrentSession();
        Account account = null;
        String name = signUpForm.getUserName();
        boolean isNewAccount = false;

        if(name != null){
            account = this.findAccount(name);
        }
        if(account == null){
            isNewAccount = true;
            account = new Account();
        }
        String password = signUpForm.getPassword();
        account.setEmail(signUpForm.getEmail());
        account.setUserRole("ROLE_USER");
        account.setEncrytedPassword(bCryptPasswordEncoder.encode(password));
        account.setUserName(name);
        account.setActive(true);

        if(isNewAccount)
            session.persist(account);

        session.flush();
    }


}