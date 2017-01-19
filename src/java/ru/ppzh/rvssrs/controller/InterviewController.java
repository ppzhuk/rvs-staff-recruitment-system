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
import ru.ppzh.rvssrs.dao.ResumeJpaController;
import ru.ppzh.rvssrs.dao.VacancyJpaController;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Applicant;
import ru.ppzh.rvssrs.model.Employer;
import ru.ppzh.rvssrs.model.Resume;
import ru.ppzh.rvssrs.model.Vacancy;

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
    private ResumeJpaController resumeDao = null;
    private VacancyJpaController vacancyDao = null;
    
    public InterviewJpaController getDao() {
        if (dao == null) {
            return new InterviewJpaController(utx, emf);
        } else {
            return dao;
        }
    }
    
    
    public ResumeJpaController getResumeDao() {
        if (resumeDao == null) {
            return new ResumeJpaController(utx, emf);
        } else {
            return resumeDao;
        }
    }
    
    
    public VacancyJpaController getVacancyDao() {
        if (vacancyDao == null) {
            return new VacancyJpaController(utx, emf);
        } else {
            return vacancyDao;
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

    public void update() throws RollbackFailureException, Exception {
        if (selected.getApplicantResult() == null) {
            selected.setApplicantResult(Interview.RESULT_UNDEFINED);
        }
        if (selected.getEmployerResult() == null) {
            selected.setEmployerResult(Interview.RESULT_UNDEFINED);
        }
        
        getDao().edit(selected);
        
        if (selected.getInterviewResult() == Interview.RESULT_POSITIVE) {
       
                Resume r = selected.getApplicantId().getResumeCollection().iterator().next();
                Vacancy v = selected.getVacancyId();
                
                r.closeResume(v);
                getResumeDao().edit(r);
                v.closeVacancy(selected.getApplicantId());
                v.getResumeCollection().add(r);
                getVacancyDao().edit(v);
        }
        
            
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
    
    public boolean isSetEmployerResultDisabled() {
        if (selected == null) {
            return true;
        }
        return selected.getEmployerResult() != Interview.RESULT_UNDEFINED ||
                !selected.isInterviewPassed() || 
                loginController.getLoginPerson().getApplicant() != null;
    }
    
    public boolean isSetAplicantResultDisabled() {
        if (selected == null) {
            return true;
        }
        return selected.getApplicantResult() != Interview.RESULT_UNDEFINED ||
                !selected.isInterviewPassed() || 
                loginController.getLoginPerson().getEmployer() != null;
    }
}
