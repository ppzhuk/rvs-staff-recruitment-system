/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.ApplicantJpaController;
import ru.ppzh.rvssrs.dao.EmployerJpaController;
import ru.ppzh.rvssrs.dao.PersonJpaController;
import ru.ppzh.rvssrs.dao.ResumeJpaController;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Applicant;
import ru.ppzh.rvssrs.model.Employer;
import ru.ppzh.rvssrs.model.Person;
import ru.ppzh.rvssrs.model.Resume;

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
    private ResumeJpaController resumeDao = null;
    
    public ResumeJpaController getResumeDao() {
        if (resumeDao == null) {
            return new ResumeJpaController(utx, emf);
        } else {
            return resumeDao;
        }
    }
    
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
    
    private Person createNewPerson() {
        Person p = new Person();
        p.setLogin(login);
        p.setPassword(pass);
        p.setName(name);
        p.setEmail(email);
        try {
            getPersonDao().create(p);
        } catch (Exception ex) {
            Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("person id after save to db: "+p.getId());
        return p;
    }
    
    public String createNewApplicant() {
        Person p = createNewPerson();
        Applicant a = new Applicant();
        a.setPersonId(p);
        try {
            getApplicantDao().create(a);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        createNewResume(a);
        return "index";
    }
    
    private Resume createNewResume(Applicant applicantId) {
        Resume r = new Resume();
        r.setApplicantId(applicantId);
        r.setSkills(skills);
        r.setEducation(education);
        r.setExperience(expertise);
        r.setDescription(description);
        try {
            getResumeDao().create(r);
        } catch (Exception ex) {
            Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }
    
    public String createNewEmployer() {
        Person p = createNewPerson();
        Employer e = new Employer();
        e.setPersonId(p);
        e.setCompanyName(companyName);
        e.setDescription(description);
        e.setSite(site);
        try {
            getEmployerDao().create(e);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "index";
    }
}
