/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.EmployerJpaController;
import ru.ppzh.rvssrs.dao.PersonJpaController;
import ru.ppzh.rvssrs.dao.exceptions.NonexistentEntityException;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.facade.PersonFacade;
import ru.ppzh.rvssrs.model.Employer;
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
    
    private Person loginPerson;
    
    @PersistenceUnit(unitName="rvs-staff-recruitment-systemPU")
    EntityManagerFactory emf; 
    @Resource 
    UserTransaction utx;
    
    private PersonJpaController dao = null;
    private EmployerJpaController employerDao = null;  
    
    public PersonJpaController getDao() {
        if (dao == null) {
            return new PersonJpaController(utx, emf);
        } else {
            return dao;
        }
    }
    
    public EmployerJpaController getEmployerDao() {
        if (employerDao == null) {
            return new EmployerJpaController(utx, emf);
        } else {
            return employerDao;
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
            log("tryToLogin");
            return "main";
        } catch (NoResultException e) {
            setLoginResultMsg("Wrong login or password.");
            return "error";
        } catch (Exception e) {
            setLoginResultMsg("Smth went wrong: " + e.getMessage());
            return "error";
        }
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
        log("getLoginPerson");
        return loginPerson;
    }

    public void setLoginPerson(Person loginPerson) {
        this.loginPerson = loginPerson;
    }
    
    public String updatePersonalData() {
        Employer e = loginPerson.getEmployer();
        try {
            if (e != null) {
                    getEmployerDao().edit(e);
            } 
            getDao().edit(loginPerson);
            
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "main";
    }
    
    public void log(String name) {
        StringBuilder sb = new StringBuilder("");
        sb.append(name + "  -  ");
        if (login != null) {
            sb.append("login: " + login + ", ");
        } else {
            sb.append("login: " + null + ", ");
        }
        if (password != null) {
            sb.append("password: " + password + ", ");
        } else {
            sb.append("password: " + null + ", ");
        }
        if (loginPerson != null) {
            sb.append("loginPerson: " + loginPerson.toString() + ", ");
        } else {
            sb.append("loginPerson: " + null + ", ");
        }
        if (loginResultMsg != null) {
            sb.append("loginResultMsg: " + loginResultMsg + ", ");
        } else {
            sb.append("loginResultMsg: " + null + ", ");
        }

        if (personFacade != null) {
            sb.append("personFacade: " + personFacade.toString() + ", ");
        } else {
            sb.append("personFacade: " + null + ", ");
        }
        if (dao != null) {
            sb.append("personDao: " + dao.toString() + ", ");
        } else {
            sb.append("personDao: " + null + ", ");
        }
        System.out.println(sb.toString());
    }
}
