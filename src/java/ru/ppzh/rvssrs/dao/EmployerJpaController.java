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
import ru.ppzh.rvssrs.model.Vacancy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.exceptions.IllegalOrphanException;
import ru.ppzh.rvssrs.dao.exceptions.NonexistentEntityException;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Employer;

/**
 *
 * @author Nataly
 */
public class EmployerJpaController implements Serializable {

    public EmployerJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Employer employer) throws IllegalOrphanException, RollbackFailureException, Exception {
        if (employer.getVacancyCollection() == null) {
            employer.setVacancyCollection(new ArrayList<Vacancy>());
        }
        List<String> illegalOrphanMessages = null;
        Person personIdOrphanCheck = employer.getPersonId();
        if (personIdOrphanCheck != null) {
            Employer oldEmployerOfPersonId = personIdOrphanCheck.getEmployer();
            if (oldEmployerOfPersonId != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Person " + personIdOrphanCheck + " already has an item of type Employer whose personId column cannot be null. Please make another selection for the personId field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Person personId = employer.getPersonId();
            if (personId != null) {
                personId = em.getReference(personId.getClass(), personId.getId());
                employer.setPersonId(personId);
            }
            Collection<Vacancy> attachedVacancyCollection = new ArrayList<Vacancy>();
            for (Vacancy vacancyCollectionVacancyToAttach : employer.getVacancyCollection()) {
                vacancyCollectionVacancyToAttach = em.getReference(vacancyCollectionVacancyToAttach.getClass(), vacancyCollectionVacancyToAttach.getId());
                attachedVacancyCollection.add(vacancyCollectionVacancyToAttach);
            }
            employer.setVacancyCollection(attachedVacancyCollection);
            em.persist(employer);
            if (personId != null) {
                personId.setEmployer(employer);
                personId = em.merge(personId);
            }
            for (Vacancy vacancyCollectionVacancy : employer.getVacancyCollection()) {
                Employer oldEmployerIdOfVacancyCollectionVacancy = vacancyCollectionVacancy.getEmployerId();
                vacancyCollectionVacancy.setEmployerId(employer);
                vacancyCollectionVacancy = em.merge(vacancyCollectionVacancy);
                if (oldEmployerIdOfVacancyCollectionVacancy != null) {
                    oldEmployerIdOfVacancyCollectionVacancy.getVacancyCollection().remove(vacancyCollectionVacancy);
                    oldEmployerIdOfVacancyCollectionVacancy = em.merge(oldEmployerIdOfVacancyCollectionVacancy);
                }
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

    public void edit(Employer employer) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Employer persistentEmployer = em.find(Employer.class, employer.getId());
            Person personIdOld = persistentEmployer.getPersonId();
            Person personIdNew = employer.getPersonId();
            Collection<Vacancy> vacancyCollectionOld = persistentEmployer.getVacancyCollection();
            Collection<Vacancy> vacancyCollectionNew = employer.getVacancyCollection();
            List<String> illegalOrphanMessages = null;
            if (personIdNew != null && !personIdNew.equals(personIdOld)) {
                Employer oldEmployerOfPersonId = personIdNew.getEmployer();
                if (oldEmployerOfPersonId != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Person " + personIdNew + " already has an item of type Employer whose personId column cannot be null. Please make another selection for the personId field.");
                }
            }
            for (Vacancy vacancyCollectionOldVacancy : vacancyCollectionOld) {
                if (!vacancyCollectionNew.contains(vacancyCollectionOldVacancy)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Vacancy " + vacancyCollectionOldVacancy + " since its employerId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (personIdNew != null) {
                personIdNew = em.getReference(personIdNew.getClass(), personIdNew.getId());
                employer.setPersonId(personIdNew);
            }
            Collection<Vacancy> attachedVacancyCollectionNew = new ArrayList<Vacancy>();
            for (Vacancy vacancyCollectionNewVacancyToAttach : vacancyCollectionNew) {
                vacancyCollectionNewVacancyToAttach = em.getReference(vacancyCollectionNewVacancyToAttach.getClass(), vacancyCollectionNewVacancyToAttach.getId());
                attachedVacancyCollectionNew.add(vacancyCollectionNewVacancyToAttach);
            }
            vacancyCollectionNew = attachedVacancyCollectionNew;
            employer.setVacancyCollection(vacancyCollectionNew);
            employer = em.merge(employer);
            if (personIdOld != null && !personIdOld.equals(personIdNew)) {
                personIdOld.setEmployer(null);
                personIdOld = em.merge(personIdOld);
            }
            if (personIdNew != null && !personIdNew.equals(personIdOld)) {
                personIdNew.setEmployer(employer);
                personIdNew = em.merge(personIdNew);
            }
            for (Vacancy vacancyCollectionNewVacancy : vacancyCollectionNew) {
                if (!vacancyCollectionOld.contains(vacancyCollectionNewVacancy)) {
                    Employer oldEmployerIdOfVacancyCollectionNewVacancy = vacancyCollectionNewVacancy.getEmployerId();
                    vacancyCollectionNewVacancy.setEmployerId(employer);
                    vacancyCollectionNewVacancy = em.merge(vacancyCollectionNewVacancy);
                    if (oldEmployerIdOfVacancyCollectionNewVacancy != null && !oldEmployerIdOfVacancyCollectionNewVacancy.equals(employer)) {
                        oldEmployerIdOfVacancyCollectionNewVacancy.getVacancyCollection().remove(vacancyCollectionNewVacancy);
                        oldEmployerIdOfVacancyCollectionNewVacancy = em.merge(oldEmployerIdOfVacancyCollectionNewVacancy);
                    }
                }
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
                Integer id = employer.getId();
                if (findEmployer(id) == null) {
                    throw new NonexistentEntityException("The employer with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Employer employer;
            try {
                employer = em.getReference(Employer.class, id);
                employer.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The employer with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vacancy> vacancyCollectionOrphanCheck = employer.getVacancyCollection();
            for (Vacancy vacancyCollectionOrphanCheckVacancy : vacancyCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Employer (" + employer + ") cannot be destroyed since the Vacancy " + vacancyCollectionOrphanCheckVacancy + " in its vacancyCollection field has a non-nullable employerId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Person personId = employer.getPersonId();
            if (personId != null) {
                personId.setEmployer(null);
                personId = em.merge(personId);
            }
            em.remove(employer);
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

    public List<Employer> findEmployerEntities() {
        return findEmployerEntities(true, -1, -1);
    }

    public List<Employer> findEmployerEntities(int maxResults, int firstResult) {
        return findEmployerEntities(false, maxResults, firstResult);
    }

    private List<Employer> findEmployerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Employer.class));
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

    public Employer findEmployer(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Employer.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmployerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Employer> rt = cq.from(Employer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
