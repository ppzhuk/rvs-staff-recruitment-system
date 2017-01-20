/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.controller.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Pavel
 */
@FacesValidator(value ="markValidator")
public class MarkValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Integer mark = (Integer) value;
        if (mark < 1 || mark > 5) {
                FacesMessage facesMessage = 
                        new FacesMessage(((HtmlInputText) component).getLabel() + ": shoul be in range 1-5");
                throw new ValidatorException(facesMessage);
        }
    }
    
}
