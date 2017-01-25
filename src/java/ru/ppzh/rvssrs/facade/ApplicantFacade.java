/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.facade;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ru.ppzh.rvssrs.model.Applicant;

/**
 *
 * @author Nataly
 */
@Stateless
public class ApplicantFacade extends AbstractFacade<Applicant> {

    @PersistenceContext(unitName = "rvs-staff-recruitment-systemPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ApplicantFacade() {
        super(Applicant.class);
    }
    
    public List<Applicant> getFreeApplicants() {
       List<Applicant> list = findAll();
       List<Applicant> newList = new ArrayList<>();
       for (Applicant a: list) {
           if (a.getResumeCollection().iterator().next().getInSearch()) {
               newList.add(a);
           }
       }
       return newList;
    }
    
}
