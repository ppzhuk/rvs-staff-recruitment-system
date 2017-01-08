/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ru.ppzh.rvssrs.model.Person;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.exceptions.IllegalOrphanException;
import ru.ppzh.rvssrs.dao.exceptions.NonexistentEntityException;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Manager;

/**
 *
 * @author Nataly
 */
public class ManagerJpaController implements Serializable {

    public ManagerJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Manager manager) throws IllegalOrphanException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Person personIdOrphanCheck = manager.getPersonId();
        if (personIdOrphanCheck != null) {
            Manager oldManagerOfPersonId = personIdOrphanCheck.getManager();
            if (oldManagerOfPersonId != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Person " + personIdOrphanCheck + " already has an item of type Manager whose personId column cannot be null. Please make another selection for the personId field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Person personId = manager.getPersonId();
            if (personId != null) {
                personId = em.getReference(personId.getClass(), personId.getId());
                manager.setPersonId(personId);
            }
            em.persist(manager);
            if (personId != null) {
                personId.setManager(manager);
                personId = em.merge(personId);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Manager manager) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Manager persistentManager = em.find(Manager.class, manager.getId());
            Person personIdOld = persistentManager.getPersonId();
            Person personIdNew = manager.getPersonId();
            List<String> illegalOrphanMessages = null;
            if (personIdNew != null && !personIdNew.equals(personIdOld)) {
                Manager oldManagerOfPersonId = personIdNew.getManager();
                if (oldManagerOfPersonId != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Person " + personIdNew + " already has an item of type Manager whose personId column cannot be null. Please make another selection for the personId field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (personIdNew != null) {
                personIdNew = em.getReference(personIdNew.getClass(), personIdNew.getId());
                manager.setPersonId(personIdNew);
            }
            manager = em.merge(manager);
            if (personIdOld != null && !personIdOld.equals(personIdNew)) {
                personIdOld.setManager(null);
                personIdOld = em.merge(personIdOld);
            }
            if (personIdNew != null && !personIdNew.equals(personIdOld)) {
                personIdNew.setManager(manager);
                personIdNew = em.merge(personIdNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = manager.getId();
                if (findManager(id) == null) {
                    throw new NonexistentEntityException("The manager with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Manager manager;
            try {
                manager = em.getReference(Manager.class, id);
                manager.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The manager with id " + id + " no longer exists.", enfe);
            }
            Person personId = manager.getPersonId();
            if (personId != null) {
                personId.setManager(null);
                personId = em.merge(personId);
            }
            em.remove(manager);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Manager> findManagerEntities() {
        return findManagerEntities(true, -1, -1);
    }

    public List<Manager> findManagerEntities(int maxResults, int firstResult) {
        return findManagerEntities(false, maxResults, firstResult);
    }

    private List<Manager> findManagerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Manager.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Manager findManager(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Manager.class, id);
        } finally {
            em.close();
        }
    }

    public int getManagerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Manager> rt = cq.from(Manager.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
