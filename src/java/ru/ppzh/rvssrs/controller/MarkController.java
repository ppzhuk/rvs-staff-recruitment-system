package ru.ppzh.rvssrs.controller;

import ru.ppzh.rvssrs.model.Mark;
import ru.ppzh.rvssrs.controller.util.JsfUtil;
import ru.ppzh.rvssrs.controller.util.JsfUtil.PersistAction;
import ru.ppzh.rvssrs.facade.MarkFacade;

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
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.MarkJpaController;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;

@Named("markController")
@SessionScoped
public class MarkController implements Serializable {

    @EJB
    private ru.ppzh.rvssrs.facade.MarkFacade ejbFacade;
    private List<Mark> items = null;
    private Mark selected;

    @Inject private LoginController loginController;
    
    @PersistenceUnit(unitName="rvs-staff-recruitment-systemPU")
    EntityManagerFactory emf; 
    @Resource 
    UserTransaction utx;
    
    private MarkJpaController dao = null;
    
    public MarkJpaController getDao() {
        if (dao == null) {
            return new MarkJpaController(utx, emf);
        } else {
            return dao;
        }
    }
    
    public MarkController() {
    }

    public Mark getSelected() {
        return selected;
    }

    public void setSelected(Mark selected) {
        this.selected = selected;
    }
    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MarkFacade getFacade() {
        return ejbFacade;
    }

    public Mark prepareCreate() {
        selected = new Mark();
        return selected;
    }

    public void create() {
        selected.setManagerId(
                loginController.getLoginPerson().getManager().getId()
        );
        try {
            getDao().create(selected);
        } catch (Exception ex) {
            Logger.getLogger(MarkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void update() {
        try {
            getDao().edit(selected);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(MarkController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MarkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy() {
        try {
            getDao().destroy(selected.getId());
        } catch (RollbackFailureException ex) {
            Logger.getLogger(MarkController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MarkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Mark> getItems() {
        int managerId = loginController.getLoginPerson().getManager().getId();
        return getDao().getMarksByManagerId(managerId);
    }

   
    public Mark getMark(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Mark> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Mark> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Mark.class)
    public static class MarkControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MarkController controller = (MarkController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "markController");
            return controller.getMark(getKey(value));
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
            if (object instanceof Mark) {
                Mark o = (Mark) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Mark.class.getName()});
                return null;
            }
        }

    }

}
