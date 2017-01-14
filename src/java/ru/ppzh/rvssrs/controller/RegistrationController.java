/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.controller;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.ApplicantJpaController;
import ru.ppzh.rvssrs.dao.EmployerJpaController;
import ru.ppzh.rvssrs.dao.PersonJpaController;

@Named(value = "registrationController")
@RequestScoped
public class RegistrationController {

    private String name;
    private String login;
    private String pass;
    private String passConfirm;
    private String email;
    private String description;
    
    //applicant data
    private String expertise;    
    private String skills;
    private String education;
    
    //employer data
    private String companyName;
    private String site;

    @PersistenceUnit(unitName="rvs-staff-recruitment-systemPU")
    EntityManagerFactory emf; 
    @Resource 
    UserTransaction utx;
    
    private PersonJpaController personDao = null;
    private ApplicantJpaController applicantDao = null;    
    private EmployerJpaController employerDao = null;
    
    public PersonJpaController getPersonDao() {
        if (personDao == null) {
            return new PersonJpaController(utx, emf);
        } else {
            return personDao;
        }
    }
    
    public ApplicantJpaController getApplicantDao() {
        if (applicantDao == null) {
            return new ApplicantJpaController(utx, emf);
        } else {
            return applicantDao;
        }
    }
    
    public EmployerJpaController getEmployerDao() {
        if (employerDao == null) {
            return new EmployerJpaController(utx, emf);
        } else {
            return employerDao;
        }
    }
    
    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }


    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }


    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassConfirm() {
        return passConfirm;
    }

    public void setPassConfirm(String passConfirm) {
        this.passConfirm = passConfirm;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public RegistrationController() {
    }
    
    private boolean createNewPerson() {
        return true;
    }
    
    public String createNewApplicant() {
        return "";
    }
    
    public String createNewEmployer() {
        return "";
    }
}
