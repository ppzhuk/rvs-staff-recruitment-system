/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ru.ppzh.rvssrs.model.Manager;

/**
 *
 * @author Nataly
 */
@Stateless
public class ManagerFacade extends AbstractFacade<Manager> {

    @PersistenceContext(unitName = "rvs-staff-recruitment-systemPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ManagerFacade() {
        super(Manager.class);
    }
    
}
