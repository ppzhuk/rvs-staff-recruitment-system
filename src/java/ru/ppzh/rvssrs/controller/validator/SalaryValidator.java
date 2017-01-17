/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.controller.validator;

import java.math.BigDecimal;
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
@FacesValidator(value ="salaryValidator")
public class SalaryValidator implements Validator{

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        BigDecimal salary = (BigDecimal) value;
        if (salary.compareTo(new BigDecimal(0)) < 0) {
                FacesMessage facesMessage = 
                        new FacesMessage(((HtmlInputText) component).getLabel() + ": salary can't be negative");
                throw new ValidatorException(facesMessage);
        }
    }
    
}
