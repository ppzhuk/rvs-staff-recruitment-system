/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.controller.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import ru.ppzh.rvssrs.model.Vacancy;

/**
 *
 * @author Pavel
 */
@FacesValidator(value ="dateValidator")
public class DateValidator implements Validator {

    @Override
    public void validate(
            FacesContext context, 
            UIComponent component, 
            Object value
    ) throws ValidatorException {
        if (!Vacancy.datePattern.matcher((CharSequence)value).matches()) {
            throw new ValidatorException(
                    new FacesMessage("Date is incorrect. Correct date format: YYYY.MM.DD")
            );
        }
    }
    
}
