package ru.ppzh.rvssrs.controller;

import ru.ppzh.rvssrs.model.Resume;
import ru.ppzh.rvssrs.controller.util.JsfUtil;
import ru.ppzh.rvssrs.controller.util.JsfUtil.PersistAction;
import ru.ppzh.rvssrs.facade.ResumeFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.ResumeJpaController;
import ru.ppzh.rvssrs.model.Applicant;

@Named("resumeController")
@SessionScoped
public class ResumeController implements Serializable {

    @EJB
    private ru.ppzh.rvssrs.facade.ResumeFacade ejbFacade;
    private List<Resume> items;
    private Resume selected;
    private String displayMode = "all";
    
    @Inject 
    private LoginController loginController;

    @PersistenceUnit(unitName="rvs-staff-recruitment-systemPU")
    EntityManagerFactory emf; 
    @Resource 
    UserTransaction utx;
    
    private ResumeJpaController dao = null;
    
    public ResumeJpaController getDao() {
        if (dao == null) {
            return new ResumeJpaController(utx, emf);
        } else {
            return dao;
        }
    }
    


    
    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    
    public ResumeController() {
    }

    public Resume getSelected() {
        return selected;
    }

    public void setSelected(Resume selected) {
        this.selected = selected;
    }


    private ResumeFacade getFacade() {
        return ejbFacade;
    }

    public Resume prepareCreate() {
        selected = new Resume();
        return selected;
    }

    public void create() {
    }

    public void update() {
    }

    public void destroy() {
    }

    public List<Resume> getItems() {
        ResumeJpaController dao = getDao();
        if (displayMode.equals("all")) {
            items = dao.findResumeEntities();
        } else if (displayMode.equals("unemployed")) {
             items = dao.getResumesByInSearch(true);
        } else if (displayMode.equals("employed")) {
             items = dao.getResumesByInSearch(false);
        } else if (displayMode.equals("own")) {
            Applicant a = loginController.getLoginPerson().getApplicant();
            if (a == null) {
                items = new ArrayList<Resume>();
            } else {
                items = dao.getVacanciesByApplicantId(a.getId());
            }
        } else {
            items = new ArrayList<Resume>();
        }
        
        return items;
    }
}
