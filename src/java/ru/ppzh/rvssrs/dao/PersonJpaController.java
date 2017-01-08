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
import ru.ppzh.rvssrs.model.Manager;
import ru.ppzh.rvssrs.model.Employer;
import ru.ppzh.rvssrs.model.Applicant;
import ru.ppzh.rvssrs.model.Mark;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.exceptions.IllegalOrphanException;
import ru.ppzh.rvssrs.dao.exceptions.NonexistentEntityException;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Person;

/**
 *
 * @author Nataly
 */
public class PersonJpaController implements Serializable {

    public PersonJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Person person) throws RollbackFailureException, Exception {
        if (person.getMarkCollection() == null) {
            person.setMarkCollection(new ArrayList<Mark>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Manager manager = person.getManager();
            if (manager != null) {
                manager = em.getReference(manager.getClass(), manager.getId());
                person.setManager(manager);
            }
            Employer employer = person.getEmployer();
            if (employer != null) {
                employer = em.getReference(employer.getClass(), employer.getId());
                person.setEmployer(employer);
            }
            Applicant applicant = person.getApplicant();
            if (applicant != null) {
                applicant = em.getReference(applicant.getClass(), applicant.getId());
                person.setApplicant(applicant);
            }
            Collection<Mark> attachedMarkCollection = new ArrayList<Mark>();
            for (Mark markCollectionMarkToAttach : person.getMarkCollection()) {
                markCollectionMarkToAttach = em.getReference(markCollectionMarkToAttach.getClass(), markCollectionMarkToAttach.getId());
                attachedMarkCollection.add(markCollectionMarkToAttach);
            }
            person.setMarkCollection(attachedMarkCollection);
            em.persist(person);
            if (manager != null) {
                Person oldPersonIdOfManager = manager.getPersonId();
                if (oldPersonIdOfManager != null) {
                    oldPersonIdOfManager.setManager(null);
                    oldPersonIdOfManager = em.merge(oldPersonIdOfManager);
                }
                manager.setPersonId(person);
                manager = em.merge(manager);
            }
            if (employer != null) {
                Person oldPersonIdOfEmployer = employer.getPersonId();
                if (oldPersonIdOfEmployer != null) {
                    oldPersonIdOfEmployer.setEmployer(null);
                    oldPersonIdOfEmployer = em.merge(oldPersonIdOfEmployer);
                }
                employer.setPersonId(person);
                employer = em.merge(employer);
            }
            if (applicant != null) {
                Person oldPersonIdOfApplicant = applicant.getPersonId();
                if (oldPersonIdOfApplicant != null) {
                    oldPersonIdOfApplicant.setApplicant(null);
                    oldPersonIdOfApplicant = em.merge(oldPersonIdOfApplicant);
                }
                applicant.setPersonId(person);
                applicant = em.merge(applicant);
            }
            for (Mark markCollectionMark : person.getMarkCollection()) {
                Person oldEvaluatedPersonIdOfMarkCollectionMark = markCollectionMark.getEvaluatedPersonId();
                markCollectionMark.setEvaluatedPersonId(person);
                markCollectionMark = em.merge(markCollectionMark);
                if (oldEvaluatedPersonIdOfMarkCollectionMark != null) {
                    oldEvaluatedPersonIdOfMarkCollectionMark.getMarkCollection().remove(markCollectionMark);
                    oldEvaluatedPersonIdOfMarkCollectionMark = em.merge(oldEvaluatedPersonIdOfMarkCollectionMark);
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

    public void edit(Person person) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Person persistentPerson = em.find(Person.class, person.getId());
            Manager managerOld = persistentPerson.getManager();
            Manager managerNew = person.getManager();
            Employer employerOld = persistentPerson.getEmployer();
            Employer employerNew = person.getEmployer();
            Applicant applicantOld = persistentPerson.getApplicant();
            Applicant applicantNew = person.getApplicant();
            Collection<Mark> markCollectionOld = persistentPerson.getMarkCollection();
            Collection<Mark> markCollectionNew = person.getMarkCollection();
            List<String> illegalOrphanMessages = null;
            if (managerOld != null && !managerOld.equals(managerNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Manager " + managerOld + " since its personId field is not nullable.");
            }
            if (employerOld != null && !employerOld.equals(employerNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Employer " + employerOld + " since its personId field is not nullable.");
            }
            if (applicantOld != null && !applicantOld.equals(applicantNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Applicant " + applicantOld + " since its personId field is not nullable.");
            }
            for (Mark markCollectionOldMark : markCollectionOld) {
                if (!markCollectionNew.contains(markCollectionOldMark)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Mark " + markCollectionOldMark + " since its evaluatedPersonId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (managerNew != null) {
                managerNew = em.getReference(managerNew.getClass(), managerNew.getId());
                person.setManager(managerNew);
            }
            if (employerNew != null) {
                employerNew = em.getReference(employerNew.getClass(), employerNew.getId());
                person.setEmployer(employerNew);
            }
            if (applicantNew != null) {
                applicantNew = em.getReference(applicantNew.getClass(), applicantNew.getId());
                person.setApplicant(applicantNew);
            }
            Collection<Mark> attachedMarkCollectionNew = new ArrayList<Mark>();
            for (Mark markCollectionNewMarkToAttach : markCollectionNew) {
                markCollectionNewMarkToAttach = em.getReference(markCollectionNewMarkToAttach.getClass(), markCollectionNewMarkToAttach.getId());
                attachedMarkCollectionNew.add(markCollectionNewMarkToAttach);
            }
            markCollectionNew = attachedMarkCollectionNew;
            person.setMarkCollection(markCollectionNew);
            person = em.merge(person);
            if (managerNew != null && !managerNew.equals(managerOld)) {
                Person oldPersonIdOfManager = managerNew.getPersonId();
                if (oldPersonIdOfManager != null) {
                    oldPersonIdOfManager.setManager(null);
                    oldPersonIdOfManager = em.merge(oldPersonIdOfManager);
                }
                managerNew.setPersonId(person);
                managerNew = em.merge(managerNew);
            }
            if (employerNew != null && !employerNew.equals(employerOld)) {
                Person oldPersonIdOfEmployer = employerNew.getPersonId();
                if (oldPersonIdOfEmployer != null) {
                    oldPersonIdOfEmployer.setEmployer(null);
                    oldPersonIdOfEmployer = em.merge(oldPersonIdOfEmployer);
                }
                employerNew.setPersonId(person);
                employerNew = em.merge(employerNew);
            }
            if (applicantNew != null && !applicantNew.equals(applicantOld)) {
                Person oldPersonIdOfApplicant = applicantNew.getPersonId();
                if (oldPersonIdOfApplicant != null) {
                    oldPersonIdOfApplicant.setApplicant(null);
                    oldPersonIdOfApplicant = em.merge(oldPersonIdOfApplicant);
                }
                applicantNew.setPersonId(person);
                applicantNew = em.merge(applicantNew);
            }
            for (Mark markCollectionNewMark : markCollectionNew) {
                if (!markCollectionOld.contains(markCollectionNewMark)) {
                    Person oldEvaluatedPersonIdOfMarkCollectionNewMark = markCollectionNewMark.getEvaluatedPersonId();
                    markCollectionNewMark.setEvaluatedPersonId(person);
                    markCollectionNewMark = em.merge(markCollectionNewMark);
                    if (oldEvaluatedPersonIdOfMarkCollectionNewMark != null && !oldEvaluatedPersonIdOfMarkCollectionNewMark.equals(person)) {
                        oldEvaluatedPersonIdOfMarkCollectionNewMark.getMarkCollection().remove(markCollectionNewMark);
                        oldEvaluatedPersonIdOfMarkCollectionNewMark = em.merge(oldEvaluatedPersonIdOfMarkCollectionNewMark);
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
                Integer id = person.getId();
                if (findPerson(id) == null) {
                    throw new NonexistentEntityException("The person with id " + id + " no longer exists.");
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
            Person person;
            try {
                person = em.getReference(Person.class, id);
                person.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The person with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Manager managerOrphanCheck = person.getManager();
            if (managerOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the Manager " + managerOrphanCheck + " in its manager field has a non-nullable personId field.");
            }
            Employer employerOrphanCheck = person.getEmployer();
            if (employerOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the Employer " + employerOrphanCheck + " in its employer field has a non-nullable personId field.");
            }
            Applicant applicantOrphanCheck = person.getApplicant();
            if (applicantOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the Applicant " + applicantOrphanCheck + " in its applicant field has a non-nullable personId field.");
            }
            Collection<Mark> markCollectionOrphanCheck = person.getMarkCollection();
            for (Mark markCollectionOrphanCheckMark : markCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the Mark " + markCollectionOrphanCheckMark + " in its markCollection field has a non-nullable evaluatedPersonId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(person);
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

    public List<Person> findPersonEntities() {
        return findPersonEntities(true, -1, -1);
    }

    public List<Person> findPersonEntities(int maxResults, int firstResult) {
        return findPersonEntities(false, maxResults, firstResult);
    }

    private List<Person> findPersonEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Person.class));
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

    public Person findPerson(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Person.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Person> rt = cq.from(Person.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
