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
import javax.faces.component.UIInput;
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

@FacesValidator(value ="passConfirmValidator")
public class PassConfirmValidator implements Validator{

    @Override
    public void validate(
            FacesContext context, 
            UIComponent component, 
            Object value
    ) throws ValidatorException {
        String field1Id = (String) component.getAttributes().get("field1Id");

        // Find the actual JSF component for the client ID.
        UIInput textInput = (UIInput) context.getViewRoot().findComponent(field1Id);
        if (textInput == null)
            throw new IllegalArgumentException(String.format("Unable to find component with id %s",field1Id));
        // Get its value, the entered text of the first field.
        String field1 = (String) textInput.getValue();

        // Cast the value of the entered text of the second field back to String.
        String confirm = (String) value;

        // Check if the first text is actually entered and compare it with second text.
        if (field1 != null && field1.length() != 0 && !field1.equals(confirm)) {
            throw new ValidatorException(new FacesMessage("Password is not equal."));
        }
    }
    
}
