/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.PersonJpaController;
import ru.ppzh.rvssrs.facade.PersonFacade;
import ru.ppzh.rvssrs.model.Person;

/**
 *
 * @author Pavel
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {
    
    @EJB
    private PersonFacade personFacade;
   
    private String login;
    private String password;
    private String loginResultMsg;
    
    private Person loginPerson = null;
    
    @PersistenceUnit(unitName="rvs-staff-recruitment-systemPU")
    EntityManagerFactory emf; 
    @Resource 
    UserTransaction utx;
    
    private PersonJpaController dao = null;
    
    public PersonJpaController getDao() {
        if (dao == null) {
            return new PersonJpaController(utx, emf);
        } else {
            return dao;
        }
    }
    
    /**
     * Creates a new instance of LoginController
     */
    public LoginController() {
    }
    
    public String tryToLogin() {
        try {
            loginPerson = getDao().findPersonByLoginAndPass(login, password);
            setLoginResultMsg("Welcome, " + loginPerson.getName() + "!");
            return "main";
        } catch (NoResultException e) {
            setLoginResultMsg("Wrong login or password.");
            return "error";
        } catch (Exception e) {
            setLoginResultMsg("Smth went wrong: " + e.getMessage());
            return "error";
        }
    }
    
    public String logout() {
        loginPerson = null;
        loginResultMsg = "";
        return "index";
    }

    public String getLoginResultMsg() {
        return loginResultMsg;
    }

    public void setLoginResultMsg(String loginResultMsg) {
        this.loginResultMsg = loginResultMsg;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Person getLoginPerson() {
        return loginPerson;
    }

    public void setLoginPerson(Person loginPerson) {
        this.loginPerson = loginPerson;
    }
}
