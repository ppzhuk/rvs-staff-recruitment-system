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
import ru.ppzh.rvssrs.model.Resume;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.exceptions.IllegalOrphanException;
import ru.ppzh.rvssrs.dao.exceptions.NonexistentEntityException;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Applicant;
import ru.ppzh.rvssrs.model.Interview;
import ru.ppzh.rvssrs.model.Vacancy;

/**
 *
 * @author Nataly
 */
public class ApplicantJpaController implements Serializable {

    public ApplicantJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Applicant applicant) throws IllegalOrphanException, RollbackFailureException, Exception {
        if (applicant.getResumeCollection() == null) {
            applicant.setResumeCollection(new ArrayList<Resume>());
        }
        if (applicant.getInterviewCollection() == null) {
            applicant.setInterviewCollection(new ArrayList<Interview>());
        }
        if (applicant.getVacancyCollection() == null) {
            applicant.setVacancyCollection(new ArrayList<Vacancy>());
        }
        List<String> illegalOrphanMessages = null;
        Person personIdOrphanCheck = applicant.getPersonId();
        if (personIdOrphanCheck != null) {
            Applicant oldApplicantOfPersonId = personIdOrphanCheck.getApplicant();
            if (oldApplicantOfPersonId != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Person " + personIdOrphanCheck + " already has an item of type Applicant whose personId column cannot be null. Please make another selection for the personId field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Person personId = applicant.getPersonId();
            if (personId != null) {
                personId = em.getReference(personId.getClass(), personId.getId());
                applicant.setPersonId(personId);
            }
            Collection<Resume> attachedResumeCollection = new ArrayList<Resume>();
            for (Resume resumeCollectionResumeToAttach : applicant.getResumeCollection()) {
                resumeCollectionResumeToAttach = em.getReference(resumeCollectionResumeToAttach.getClass(), resumeCollectionResumeToAttach.getId());
                attachedResumeCollection.add(resumeCollectionResumeToAttach);
            }
            applicant.setResumeCollection(attachedResumeCollection);
            Collection<Interview> attachedInterviewCollection = new ArrayList<Interview>();
            for (Interview interviewCollectionInterviewToAttach : applicant.getInterviewCollection()) {
                interviewCollectionInterviewToAttach = em.getReference(interviewCollectionInterviewToAttach.getClass(), interviewCollectionInterviewToAttach.getId());
                attachedInterviewCollection.add(interviewCollectionInterviewToAttach);
            }
            applicant.setInterviewCollection(attachedInterviewCollection);
            Collection<Vacancy> attachedVacancyCollection = new ArrayList<Vacancy>();
            for (Vacancy vacancyCollectionVacancyToAttach : applicant.getVacancyCollection()) {
                vacancyCollectionVacancyToAttach = em.getReference(vacancyCollectionVacancyToAttach.getClass(), vacancyCollectionVacancyToAttach.getId());
                attachedVacancyCollection.add(vacancyCollectionVacancyToAttach);
            }
            applicant.setVacancyCollection(attachedVacancyCollection);
            em.persist(applicant);
            if (personId != null) {
                personId.setApplicant(applicant);
                personId = em.merge(personId);
            }
            for (Resume resumeCollectionResume : applicant.getResumeCollection()) {
                Applicant oldApplicantIdOfResumeCollectionResume = resumeCollectionResume.getApplicantId();
                resumeCollectionResume.setApplicantId(applicant);
                resumeCollectionResume = em.merge(resumeCollectionResume);
                if (oldApplicantIdOfResumeCollectionResume != null) {
                    oldApplicantIdOfResumeCollectionResume.getResumeCollection().remove(resumeCollectionResume);
                    oldApplicantIdOfResumeCollectionResume = em.merge(oldApplicantIdOfResumeCollectionResume);
                }
            }
            for (Interview interviewCollectionInterview : applicant.getInterviewCollection()) {
                Applicant oldApplicantIdOfInterviewCollectionInterview = interviewCollectionInterview.getApplicantId();
                interviewCollectionInterview.setApplicantId(applicant);
                interviewCollectionInterview = em.merge(interviewCollectionInterview);
                if (oldApplicantIdOfInterviewCollectionInterview != null) {
                    oldApplicantIdOfInterviewCollectionInterview.getInterviewCollection().remove(interviewCollectionInterview);
                    oldApplicantIdOfInterviewCollectionInterview = em.merge(oldApplicantIdOfInterviewCollectionInterview);
                }
            }
            for (Vacancy vacancyCollectionVacancy : applicant.getVacancyCollection()) {
                Applicant oldApplicantIdOfVacancyCollectionVacancy = vacancyCollectionVacancy.getApplicantId();
                vacancyCollectionVacancy.setApplicantId(applicant);
                vacancyCollectionVacancy = em.merge(vacancyCollectionVacancy);
                if (oldApplicantIdOfVacancyCollectionVacancy != null) {
                    oldApplicantIdOfVacancyCollectionVacancy.getVacancyCollection().remove(vacancyCollectionVacancy);
                    oldApplicantIdOfVacancyCollectionVacancy = em.merge(oldApplicantIdOfVacancyCollectionVacancy);
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

    public void edit(Applicant applicant) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Applicant persistentApplicant = em.find(Applicant.class, applicant.getId());
            Person personIdOld = persistentApplicant.getPersonId();
            Person personIdNew = applicant.getPersonId();
            Collection<Resume> resumeCollectionOld = persistentApplicant.getResumeCollection();
            Collection<Resume> resumeCollectionNew = applicant.getResumeCollection();
            Collection<Interview> interviewCollectionOld = persistentApplicant.getInterviewCollection();
            Collection<Interview> interviewCollectionNew = applicant.getInterviewCollection();
            Collection<Vacancy> vacancyCollectionOld = persistentApplicant.getVacancyCollection();
            Collection<Vacancy> vacancyCollectionNew = applicant.getVacancyCollection();
            List<String> illegalOrphanMessages = null;
            if (personIdNew != null && !personIdNew.equals(personIdOld)) {
                Applicant oldApplicantOfPersonId = personIdNew.getApplicant();
                if (oldApplicantOfPersonId != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Person " + personIdNew + " already has an item of type Applicant whose personId column cannot be null. Please make another selection for the personId field.");
                }
            }
            for (Resume resumeCollectionOldResume : resumeCollectionOld) {
                if (!resumeCollectionNew.contains(resumeCollectionOldResume)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Resume " + resumeCollectionOldResume + " since its applicantId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (personIdNew != null) {
                personIdNew = em.getReference(personIdNew.getClass(), personIdNew.getId());
                applicant.setPersonId(personIdNew);
            }
            Collection<Resume> attachedResumeCollectionNew = new ArrayList<Resume>();
            for (Resume resumeCollectionNewResumeToAttach : resumeCollectionNew) {
                resumeCollectionNewResumeToAttach = em.getReference(resumeCollectionNewResumeToAttach.getClass(), resumeCollectionNewResumeToAttach.getId());
                attachedResumeCollectionNew.add(resumeCollectionNewResumeToAttach);
            }
            resumeCollectionNew = attachedResumeCollectionNew;
            applicant.setResumeCollection(resumeCollectionNew);
            Collection<Interview> attachedInterviewCollectionNew = new ArrayList<Interview>();
            for (Interview interviewCollectionNewInterviewToAttach : interviewCollectionNew) {
                interviewCollectionNewInterviewToAttach = em.getReference(interviewCollectionNewInterviewToAttach.getClass(), interviewCollectionNewInterviewToAttach.getId());
                attachedInterviewCollectionNew.add(interviewCollectionNewInterviewToAttach);
            }
            interviewCollectionNew = attachedInterviewCollectionNew;
            applicant.setInterviewCollection(interviewCollectionNew);
            Collection<Vacancy> attachedVacancyCollectionNew = new ArrayList<Vacancy>();
            for (Vacancy vacancyCollectionNewVacancyToAttach : vacancyCollectionNew) {
                vacancyCollectionNewVacancyToAttach = em.getReference(vacancyCollectionNewVacancyToAttach.getClass(), vacancyCollectionNewVacancyToAttach.getId());
                attachedVacancyCollectionNew.add(vacancyCollectionNewVacancyToAttach);
            }
            vacancyCollectionNew = attachedVacancyCollectionNew;
            applicant.setVacancyCollection(vacancyCollectionNew);
            applicant = em.merge(applicant);
            if (personIdOld != null && !personIdOld.equals(personIdNew)) {
                personIdOld.setApplicant(null);
                personIdOld = em.merge(personIdOld);
            }
            if (personIdNew != null && !personIdNew.equals(personIdOld)) {
                personIdNew.setApplicant(applicant);
                personIdNew = em.merge(personIdNew);
            }
            for (Resume resumeCollectionNewResume : resumeCollectionNew) {
                if (!resumeCollectionOld.contains(resumeCollectionNewResume)) {
                    Applicant oldApplicantIdOfResumeCollectionNewResume = resumeCollectionNewResume.getApplicantId();
                    resumeCollectionNewResume.setApplicantId(applicant);
                    resumeCollectionNewResume = em.merge(resumeCollectionNewResume);
                    if (oldApplicantIdOfResumeCollectionNewResume != null && !oldApplicantIdOfResumeCollectionNewResume.equals(applicant)) {
                        oldApplicantIdOfResumeCollectionNewResume.getResumeCollection().remove(resumeCollectionNewResume);
                        oldApplicantIdOfResumeCollectionNewResume = em.merge(oldApplicantIdOfResumeCollectionNewResume);
                    }
                }
            }
            for (Interview interviewCollectionOldInterview : interviewCollectionOld) {
                if (!interviewCollectionNew.contains(interviewCollectionOldInterview)) {
                    interviewCollectionOldInterview.setApplicantId(null);
                    interviewCollectionOldInterview = em.merge(interviewCollectionOldInterview);
                }
            }
            for (Interview interviewCollectionNewInterview : interviewCollectionNew) {
                if (!interviewCollectionOld.contains(interviewCollectionNewInterview)) {
                    Applicant oldApplicantIdOfInterviewCollectionNewInterview = interviewCollectionNewInterview.getApplicantId();
                    interviewCollectionNewInterview.setApplicantId(applicant);
                    interviewCollectionNewInterview = em.merge(interviewCollectionNewInterview);
                    if (oldApplicantIdOfInterviewCollectionNewInterview != null && !oldApplicantIdOfInterviewCollectionNewInterview.equals(applicant)) {
                        oldApplicantIdOfInterviewCollectionNewInterview.getInterviewCollection().remove(interviewCollectionNewInterview);
                        oldApplicantIdOfInterviewCollectionNewInterview = em.merge(oldApplicantIdOfInterviewCollectionNewInterview);
                    }
                }
            }
            for (Vacancy vacancyCollectionOldVacancy : vacancyCollectionOld) {
                if (!vacancyCollectionNew.contains(vacancyCollectionOldVacancy)) {
                    vacancyCollectionOldVacancy.setApplicantId(null);
                    vacancyCollectionOldVacancy = em.merge(vacancyCollectionOldVacancy);
                }
            }
            for (Vacancy vacancyCollectionNewVacancy : vacancyCollectionNew) {
                if (!vacancyCollectionOld.contains(vacancyCollectionNewVacancy)) {
                    Applicant oldApplicantIdOfVacancyCollectionNewVacancy = vacancyCollectionNewVacancy.getApplicantId();
                    vacancyCollectionNewVacancy.setApplicantId(applicant);
                    vacancyCollectionNewVacancy = em.merge(vacancyCollectionNewVacancy);
                    if (oldApplicantIdOfVacancyCollectionNewVacancy != null && !oldApplicantIdOfVacancyCollectionNewVacancy.equals(applicant)) {
                        oldApplicantIdOfVacancyCollectionNewVacancy.getVacancyCollection().remove(vacancyCollectionNewVacancy);
                        oldApplicantIdOfVacancyCollectionNewVacancy = em.merge(oldApplicantIdOfVacancyCollectionNewVacancy);
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
                Integer id = applicant.getId();
                if (findApplicant(id) == null) {
                    throw new NonexistentEntityException("The applicant with id " + id + " no longer exists.");
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
            Applicant applicant;
            try {
                applicant = em.getReference(Applicant.class, id);
                applicant.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The applicant with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Resume> resumeCollectionOrphanCheck = applicant.getResumeCollection();
            for (Resume resumeCollectionOrphanCheckResume : resumeCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Applicant (" + applicant + ") cannot be destroyed since the Resume " + resumeCollectionOrphanCheckResume + " in its resumeCollection field has a non-nullable applicantId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Person personId = applicant.getPersonId();
            if (personId != null) {
                personId.setApplicant(null);
                personId = em.merge(personId);
            }
            Collection<Interview> interviewCollection = applicant.getInterviewCollection();
            for (Interview interviewCollectionInterview : interviewCollection) {
                interviewCollectionInterview.setApplicantId(null);
                interviewCollectionInterview = em.merge(interviewCollectionInterview);
            }
            Collection<Vacancy> vacancyCollection = applicant.getVacancyCollection();
            for (Vacancy vacancyCollectionVacancy : vacancyCollection) {
                vacancyCollectionVacancy.setApplicantId(null);
                vacancyCollectionVacancy = em.merge(vacancyCollectionVacancy);
            }
            em.remove(applicant);
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

    public List<Applicant> findApplicantEntities() {
        return findApplicantEntities(true, -1, -1);
    }

    public List<Applicant> findApplicantEntities(int maxResults, int firstResult) {
        return findApplicantEntities(false, maxResults, firstResult);
    }

    private List<Applicant> findApplicantEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Applicant.class));
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

    public Applicant findApplicant(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Applicant.class, id);
        } finally {
            em.close();
        }
    }

    public int getApplicantCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Applicant> rt = cq.from(Applicant.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
