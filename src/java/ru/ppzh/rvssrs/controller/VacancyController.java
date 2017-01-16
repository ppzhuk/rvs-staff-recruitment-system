package ru.ppzh.rvssrs.controller;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.VacancyJpaController;
import ru.ppzh.rvssrs.model.Vacancy;

@Named("vacancyController")
@SessionScoped
public class VacancyController implements Serializable {

    @EJB
    private ru.ppzh.rvssrs.facade.VacancyFacade ejbFacade;
    
    private String displayMode;

    @PersistenceUnit(unitName="rvs-staff-recruitment-systemPU")
    EntityManagerFactory emf; 
    @Resource 
    UserTransaction utx;
    
    private VacancyJpaController dao = null;
    
    public VacancyJpaController getDao() {
        if (dao == null) {
            return new VacancyJpaController(utx, emf);
        } else {
            return dao;
        }
    }
    
    private List<Vacancy> allVacancies;

    public List<Vacancy> getAllVacancies() {
        
        List<Vacancy> list = getDao().findVacancyEntities();
        System.out.println("!!!!!!!!!!!!  "+list.get(0).getStatus() + " "+list.get(1).getStatus()+ " "+list.get(2).getStatus());
        return list;
    }

    public void setAllVacancies(List<Vacancy> allVacancies) {
        this.allVacancies = allVacancies;
    }
    
    public VacancyController() {
    }
    

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public void log() {
        System.out.println("!!!!!!!!!!!!  "+displayMode);
    }
    
    public String getStatus(int code) {
        return (code == 1) ? "CLOSE" : "OPEN";
    }
    
}
