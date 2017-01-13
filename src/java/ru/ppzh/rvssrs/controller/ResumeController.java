package ru.ppzh.rvssrs.controller;

import ru.ppzh.rvssrs.model.Resume;
import ru.ppzh.rvssrs.controller.util.JsfUtil;
import ru.ppzh.rvssrs.controller.util.JsfUtil.PersistAction;
import ru.ppzh.rvssrs.facade.ResumeFacade;

import java.io.Serializable;
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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.ResumeJpaController;

@Named("resumeController")
@SessionScoped
public class ResumeController implements Serializable {

    @EJB
    private ru.ppzh.rvssrs.facade.ResumeFacade ejbFacade;
    private List<Resume> items = null;
    private Resume selected;

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
    
    public ResumeController() {
    }

    public Resume getSelected() {
        return selected;
    }

    public void setSelected(Resume selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ResumeFacade getFacade() {
        return ejbFacade;
    }

    public Resume prepareCreate() {
        selected = new Resume();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ResumeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ResumeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ResumeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Resume> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Resume getResume(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Resume> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Resume> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Resume.class)
    public static class ResumeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ResumeController controller = (ResumeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "resumeController");
            return controller.getResume(getKey(value));
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
            if (object instanceof Resume) {
                Resume o = (Resume) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Resume.class.getName()});
                return null;
            }
        }

    }

}
