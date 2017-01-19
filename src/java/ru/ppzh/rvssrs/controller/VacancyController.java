package ru.ppzh.rvssrs.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
import ru.ppzh.rvssrs.dao.PersonJpaController;
import ru.ppzh.rvssrs.dao.VacancyJpaController;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.facade.VacancyFacade;
import ru.ppzh.rvssrs.model.Employer;
import ru.ppzh.rvssrs.model.Person;
import ru.ppzh.rvssrs.model.Vacancy;

@Named("vacancyController")
@SessionScoped
public class VacancyController implements Serializable {

    @EJB
    private ru.ppzh.rvssrs.facade.VacancyFacade ejbFacade;
    
    private String displayMode = "all";

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
    
    private PersonJpaController personDao = null;
    
    public PersonJpaController getPersonDao() {
        if (personDao == null) {
            return new PersonJpaController(utx, emf);
        } else {
            return personDao;
        }
    }
    private List<Vacancy> vacancies;
    private Vacancy selected;

    public Vacancy getSelected() {
        log("getSelected");
        return selected;
    }

    public void setSelected(Vacancy selected) {
        log("setSelected");
        this.selected = selected;
    }
    
    @Inject 
    private LoginController loginController;

    public List<Vacancy> getVacancies() {
        VacancyJpaController dao = getDao();
        if (displayMode.equals("all")) {
            vacancies = dao.findVacancyEntities();
        } else if (displayMode.equals("opened")) {
             vacancies = dao.getVacanciesByStatus(Vacancy.STATUS_OPEN);
        } else if (displayMode.equals("closed")) {
             vacancies = dao.getVacanciesByStatus(Vacancy.STATUS_CLOSE);
        } else if (displayMode.equals("own")) {
            Employer e = loginController.getLoginPerson().getEmployer();
            if (e == null) {
                vacancies = new ArrayList<Vacancy>();
            } else {
                vacancies = dao.getVacanciesByEmployerId(e.getId());
            }
        } else {
            vacancies = new ArrayList<Vacancy>();
        }
        
        return vacancies;
    }

    public void setVacancies(List<Vacancy> vacancies) {
        this.vacancies = vacancies;
    }


    
    public VacancyController() {
    }
    

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    
    public String getStatus(int code) {
        return (code == Vacancy.STATUS_CLOSE) ? "CLOSED" : "OPEN";
    }
    
    public Vacancy prepareCreate() {
        selected = new Vacancy();
        return selected;
    }
    
    public void create() {
        if (selected != null) {
            selected.setEmployerId(
                    loginController.getLoginPerson().getEmployer()
            );
            try {
                getDao().create(selected);
            } catch (Exception ex) {
                Logger.getLogger(VacancyController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new NullPointerException("trying to create new vacancy, but it is null");
        }    
    }
    
    public void destroy() {
        if (selected != null) {
            selected.setEmployerId(
                    loginController.getLoginPerson().getEmployer()
            );
            try {
                getDao().destroy(selected.getId());
            } catch (RollbackFailureException ex) {
                Logger.getLogger(VacancyController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(VacancyController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new NullPointerException("trying to delete new vacancy, but it is null");
        }   
    }
    
    public void update() {
        try {
            getDao().edit(selected);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(VacancyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(VacancyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void log(String name) {
        StringBuilder sb = new StringBuilder("");
        sb.append(name + "  -  ");
        if (selected != null) {
            sb.append("selected: " + selected.toString() + ", ");
        } else {
            sb.append("selected: " + null + ", ");
        }
        System.out.println(sb.toString());
    }
    
    private List<Vacancy> openVacancies;
    
    public List<Vacancy> getOpenVacancies() {
        openVacancies = getDao().getOpenVacancies();
        return openVacancies;
    }
    
    public Vacancy getVacancy(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Vacancy> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Vacancy> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    private VacancyFacade getFacade() {
        return ejbFacade;
    }

    @FacesConverter(forClass = Vacancy.class)
    public static class VacancyControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VacancyController controller = (VacancyController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "vacancyController");
            return controller.getVacancy(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Vacancy) {
                Vacancy o = (Vacancy) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Vacancy.class.getName()});
                return null;
            }
        }

    }
}
