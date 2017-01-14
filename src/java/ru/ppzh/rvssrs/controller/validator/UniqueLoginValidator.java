/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.controller.validator;

import java.util.List;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.PersonJpaController;
import ru.ppzh.rvssrs.model.Person;

@FacesValidator(value ="uniqueLoginValidator")
public class UniqueLoginValidator implements Validator{
    @PersistenceUnit(unitName="rvs-staff-recruitment-systemPU")
    EntityManagerFactory emf; 
    @Resource 
    UserTransaction utx;
    
    @Override
    public void validate(
            FacesContext context, 
            UIComponent component,
            Object value
    ) throws ValidatorException {
        CharSequence login = (CharSequence) value;
        PersonJpaController dao = new PersonJpaController(utx, emf);
        List<Person> persons = dao.findPersonEntities();
        for(Person p: persons) {
            if (p.getLogin().equals(login)) {
                FacesMessage facesMessage = 
                        new FacesMessage("Login is already taken");
                throw new ValidatorException(facesMessage);
            }
        }
    }
    
}
