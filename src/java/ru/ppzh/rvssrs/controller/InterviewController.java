package ru.ppzh.rvssrs.controller;

import ru.ppzh.rvssrs.model.Interview;
import ru.ppzh.rvssrs.controller.util.JsfUtil;
import ru.ppzh.rvssrs.controller.util.JsfUtil.PersistAction;
import ru.ppzh.rvssrs.facade.InterviewFacade;

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
import ru.ppzh.rvssrs.dao.InterviewJpaController;
import ru.ppzh.rvssrs.model.Applicant;
import ru.ppzh.rvssrs.model.Employer;

@Named("interviewController")
@SessionScoped
public class InterviewController implements Serializable {

    @Inject private LoginController loginController;
    
    @EJB
    private ru.ppzh.rvssrs.facade.InterviewFacade ejbFacade;
    private List<Interview> items = null;
    private Interview selected;

    @PersistenceUnit(unitName="rvs-staff-recruitment-systemPU")
    EntityManagerFactory emf; 
    @Resource 
    UserTransaction utx;
    
    private InterviewJpaController dao = null;
    
    public InterviewJpaController getDao() {
        if (dao == null) {
            return new InterviewJpaController(utx, emf);
        } else {
            return dao;
        }
    }
    
    private String displayMode = "all";

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    
    public InterviewController() {
    }

    public Interview getSelected() {
        return selected;
    }

    public void setSelected(Interview selected) {
        this.selected = selected;
    }

    private InterviewFacade getFacade() {
        return ejbFacade;
    }

    public Interview prepareCreate() {
        selected = new Interview();
        return selected;
    }

    public void create() {
    }

    public void update() {
        
    }

    public void destroy() {
    }

    public List<Interview> getItems() {
        InterviewJpaController dao = getDao();
            
        if (displayMode.equals("all")) {
            items = dao.findInterviewEntities();
        } else if (displayMode.equals("assigned")) {
             items = dao.getFutureInterviews();
        } else if (displayMode.equals("past")) {
             items = dao.getPassedInterviews();
        } else {
            items = new ArrayList<Interview>();
        }
        
        Applicant a = loginController.getLoginPerson().getApplicant();
        Employer e = loginController.getLoginPerson().getEmployer();
        filterInterviews(a, e);
            
        return items;
    }   
    
    public String getInterviewResult(Interview i) {
        if (i.getInterviewResult() == Interview.RESULT_NEGATIVE) {
            return "NEGATIVE";
        }
        if (i.getInterviewResult() == Interview.RESULT_POSITIVE) {
            return "POSITIVE";
        }
        return "UNDEFINED";
    }

    private void filterInterviews(Applicant a, Employer e) {
        if (a == null && e == null) {
            return;
        }
        List<Interview> newItems = new ArrayList<>();
        for (Interview i: items) {
            if (a != null) {
                if (i.getApplicantId().getId() == a.getId()) {
                    newItems.add(i);
                }
            } else if (e != null) {
                if (i.getVacancyId().getEmployerId().getId() == e.getId()) {
                    newItems.add(i);
                }
            }
        }
        items = newItems;
    }
}
