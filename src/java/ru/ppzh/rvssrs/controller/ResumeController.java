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
import ru.ppzh.rvssrs.dao.MarkJpaController;
import ru.ppzh.rvssrs.dao.ResumeJpaController;
import ru.ppzh.rvssrs.dao.VacancyJpaController;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Applicant;
import ru.ppzh.rvssrs.model.Mark;
import ru.ppzh.rvssrs.model.Person;
import ru.ppzh.rvssrs.model.Vacancy;

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
    
    private MarkJpaController markDao = null;
    
    public MarkJpaController getMarkDao() {
        if (markDao == null) {
            return new MarkJpaController(utx, emf);
        } else {
            return markDao;
        }
    }
    
    private VacancyJpaController vacancyDao = null;
    
    public VacancyJpaController getVacancyDao() {
        if (vacancyDao == null) {
            return new VacancyJpaController(utx, emf);
        } else {
            return vacancyDao;
        }
    }

    private Double avgMark;

    public Double getAvgMark() {
        if (selected != null) {
            Person p = selected.getApplicantId().getPersonId();
            List<Mark> marks = getMarkDao().getMarksByEvaluatedPersonId(
                    p
            );
            avgMark = p.getAverageMark(marks);
        }
        return avgMark;
    }

    public void setAvgMark(Double avgMark) {
        this.avgMark = avgMark;
    }
    
    private List<Mark> marks;

    public List<Mark> getMarks() {
        if (selected != null) {
            Person p = selected.getApplicantId().getPersonId();
            marks = getMarkDao().getMarksByEvaluatedPersonId(
                    p
            );
        }
        return marks;
    }

        private Mark selected_mark;

    public Mark getSelected_mark() {
        return selected_mark;
    }

    public void setSelected_mark(Mark selected_mark) {
        this.selected_mark = selected_mark;
    }

    
    public void setMarks(List<Mark> marks) {
        this.marks = marks;
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

    public void update() {
        try {
            
            if (selected.getInSearch()) {
                Vacancy v = selected.getVacancyId();
                if (v != null) {
                    v.setStatus(Vacancy.STATUS_OPEN);
                    v.setApplicantId(null);
                    v.setCloseDate(null);
                    getVacancyDao().edit(v);
                }
                selected.setVacancyId(null);
            }
            
            getDao().edit(selected);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(VacancyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(VacancyController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
