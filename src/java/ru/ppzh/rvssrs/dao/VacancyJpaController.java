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
import ru.ppzh.rvssrs.model.Applicant;
import ru.ppzh.rvssrs.model.Employer;
import ru.ppzh.rvssrs.model.Resume;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.exceptions.NonexistentEntityException;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Interview;
import ru.ppzh.rvssrs.model.Vacancy;

/**
 *
 * @author Nataly
 */
public class VacancyJpaController implements Serializable {

    public VacancyJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vacancy vacancy) throws RollbackFailureException, Exception {
        if (vacancy.getResumeCollection() == null) {
            vacancy.setResumeCollection(new ArrayList<Resume>());
        }
        if (vacancy.getInterviewCollection() == null) {
            vacancy.setInterviewCollection(new ArrayList<Interview>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Applicant applicantId = vacancy.getApplicantId();
            if (applicantId != null) {
                applicantId = em.getReference(applicantId.getClass(), applicantId.getId());
                vacancy.setApplicantId(applicantId);
            }
            Employer employerId = vacancy.getEmployerId();
            if (employerId != null) {
                employerId = em.getReference(employerId.getClass(), employerId.getId());
                vacancy.setEmployerId(employerId);
            }
            Collection<Resume> attachedResumeCollection = new ArrayList<Resume>();
            for (Resume resumeCollectionResumeToAttach : vacancy.getResumeCollection()) {
                resumeCollectionResumeToAttach = em.getReference(resumeCollectionResumeToAttach.getClass(), resumeCollectionResumeToAttach.getId());
                attachedResumeCollection.add(resumeCollectionResumeToAttach);
            }
            vacancy.setResumeCollection(attachedResumeCollection);
            Collection<Interview> attachedInterviewCollection = new ArrayList<Interview>();
            for (Interview interviewCollectionInterviewToAttach : vacancy.getInterviewCollection()) {
                interviewCollectionInterviewToAttach = em.getReference(interviewCollectionInterviewToAttach.getClass(), interviewCollectionInterviewToAttach.getId());
                attachedInterviewCollection.add(interviewCollectionInterviewToAttach);
            }
            vacancy.setInterviewCollection(attachedInterviewCollection);
            em.persist(vacancy);
            if (applicantId != null) {
                applicantId.getVacancyCollection().add(vacancy);
                applicantId = em.merge(applicantId);
            }
            if (employerId != null) {
                employerId.getVacancyCollection().add(vacancy);
                employerId = em.merge(employerId);
            }
            for (Resume resumeCollectionResume : vacancy.getResumeCollection()) {
                Vacancy oldVacancyIdOfResumeCollectionResume = resumeCollectionResume.getVacancyId();
                resumeCollectionResume.setVacancyId(vacancy);
                resumeCollectionResume = em.merge(resumeCollectionResume);
                if (oldVacancyIdOfResumeCollectionResume != null) {
                    oldVacancyIdOfResumeCollectionResume.getResumeCollection().remove(resumeCollectionResume);
                    oldVacancyIdOfResumeCollectionResume = em.merge(oldVacancyIdOfResumeCollectionResume);
                }
            }
            for (Interview interviewCollectionInterview : vacancy.getInterviewCollection()) {
                Vacancy oldVacancyIdOfInterviewCollectionInterview = interviewCollectionInterview.getVacancyId();
                interviewCollectionInterview.setVacancyId(vacancy);
                interviewCollectionInterview = em.merge(interviewCollectionInterview);
                if (oldVacancyIdOfInterviewCollectionInterview != null) {
                    oldVacancyIdOfInterviewCollectionInterview.getInterviewCollection().remove(interviewCollectionInterview);
                    oldVacancyIdOfInterviewCollectionInterview = em.merge(oldVacancyIdOfInterviewCollectionInterview);
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

    public void edit(Vacancy vacancy) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Vacancy persistentVacancy = em.find(Vacancy.class, vacancy.getId());
            Applicant applicantIdOld = persistentVacancy.getApplicantId();
            Applicant applicantIdNew = vacancy.getApplicantId();
            Employer employerIdOld = persistentVacancy.getEmployerId();
            Employer employerIdNew = vacancy.getEmployerId();
            Collection<Resume> resumeCollectionOld = persistentVacancy.getResumeCollection();
            Collection<Resume> resumeCollectionNew = vacancy.getResumeCollection();
            Collection<Interview> interviewCollectionOld = persistentVacancy.getInterviewCollection();
            Collection<Interview> interviewCollectionNew = vacancy.getInterviewCollection();
            if (applicantIdNew != null) {
                applicantIdNew = em.getReference(applicantIdNew.getClass(), applicantIdNew.getId());
                vacancy.setApplicantId(applicantIdNew);
            }
            if (employerIdNew != null) {
                employerIdNew = em.getReference(employerIdNew.getClass(), employerIdNew.getId());
                vacancy.setEmployerId(employerIdNew);
            }
            Collection<Resume> attachedResumeCollectionNew = new ArrayList<Resume>();
            for (Resume resumeCollectionNewResumeToAttach : resumeCollectionNew) {
                resumeCollectionNewResumeToAttach = em.getReference(resumeCollectionNewResumeToAttach.getClass(), resumeCollectionNewResumeToAttach.getId());
                attachedResumeCollectionNew.add(resumeCollectionNewResumeToAttach);
            }
            resumeCollectionNew = attachedResumeCollectionNew;
            vacancy.setResumeCollection(resumeCollectionNew);
            Collection<Interview> attachedInterviewCollectionNew = new ArrayList<Interview>();
            for (Interview interviewCollectionNewInterviewToAttach : interviewCollectionNew) {
                interviewCollectionNewInterviewToAttach = em.getReference(interviewCollectionNewInterviewToAttach.getClass(), interviewCollectionNewInterviewToAttach.getId());
                attachedInterviewCollectionNew.add(interviewCollectionNewInterviewToAttach);
            }
            interviewCollectionNew = attachedInterviewCollectionNew;
            vacancy.setInterviewCollection(interviewCollectionNew);
            vacancy = em.merge(vacancy);
            if (applicantIdOld != null && !applicantIdOld.equals(applicantIdNew)) {
                applicantIdOld.getVacancyCollection().remove(vacancy);
                applicantIdOld = em.merge(applicantIdOld);
            }
            if (applicantIdNew != null && !applicantIdNew.equals(applicantIdOld)) {
                applicantIdNew.getVacancyCollection().add(vacancy);
                applicantIdNew = em.merge(applicantIdNew);
            }
            if (employerIdOld != null && !employerIdOld.equals(employerIdNew)) {
                employerIdOld.getVacancyCollection().remove(vacancy);
                employerIdOld = em.merge(employerIdOld);
            }
            if (employerIdNew != null && !employerIdNew.equals(employerIdOld)) {
                employerIdNew.getVacancyCollection().add(vacancy);
                employerIdNew = em.merge(employerIdNew);
            }
            for (Resume resumeCollectionOldResume : resumeCollectionOld) {
                if (!resumeCollectionNew.contains(resumeCollectionOldResume)) {
                    resumeCollectionOldResume.setVacancyId(null);
                    resumeCollectionOldResume = em.merge(resumeCollectionOldResume);
                }
            }
            for (Resume resumeCollectionNewResume : resumeCollectionNew) {
                if (!resumeCollectionOld.contains(resumeCollectionNewResume)) {
                    Vacancy oldVacancyIdOfResumeCollectionNewResume = resumeCollectionNewResume.getVacancyId();
                    resumeCollectionNewResume.setVacancyId(vacancy);
                    resumeCollectionNewResume = em.merge(resumeCollectionNewResume);
                    if (oldVacancyIdOfResumeCollectionNewResume != null && !oldVacancyIdOfResumeCollectionNewResume.equals(vacancy)) {
                        oldVacancyIdOfResumeCollectionNewResume.getResumeCollection().remove(resumeCollectionNewResume);
                        oldVacancyIdOfResumeCollectionNewResume = em.merge(oldVacancyIdOfResumeCollectionNewResume);
                    }
                }
            }
            for (Interview interviewCollectionOldInterview : interviewCollectionOld) {
                if (!interviewCollectionNew.contains(interviewCollectionOldInterview)) {
                    interviewCollectionOldInterview.setVacancyId(null);
                    interviewCollectionOldInterview = em.merge(interviewCollectionOldInterview);
                }
            }
            for (Interview interviewCollectionNewInterview : interviewCollectionNew) {
                if (!interviewCollectionOld.contains(interviewCollectionNewInterview)) {
                    Vacancy oldVacancyIdOfInterviewCollectionNewInterview = interviewCollectionNewInterview.getVacancyId();
                    interviewCollectionNewInterview.setVacancyId(vacancy);
                    interviewCollectionNewInterview = em.merge(interviewCollectionNewInterview);
                    if (oldVacancyIdOfInterviewCollectionNewInterview != null && !oldVacancyIdOfInterviewCollectionNewInterview.equals(vacancy)) {
                        oldVacancyIdOfInterviewCollectionNewInterview.getInterviewCollection().remove(interviewCollectionNewInterview);
                        oldVacancyIdOfInterviewCollectionNewInterview = em.merge(oldVacancyIdOfInterviewCollectionNewInterview);
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
                Integer id = vacancy.getId();
                if (findVacancy(id) == null) {
                    throw new NonexistentEntityException("The vacancy with id " + id + " no longer exists.");
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
            Vacancy vacancy;
            try {
                vacancy = em.getReference(Vacancy.class, id);
                vacancy.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vacancy with id " + id + " no longer exists.", enfe);
            }
            Applicant applicantId = vacancy.getApplicantId();
            if (applicantId != null) {
                applicantId.getVacancyCollection().remove(vacancy);
                applicantId = em.merge(applicantId);
            }
            Employer employerId = vacancy.getEmployerId();
            if (employerId != null) {
                employerId.getVacancyCollection().remove(vacancy);
                employerId = em.merge(employerId);
            }
            Collection<Resume> resumeCollection = vacancy.getResumeCollection();
            for (Resume resumeCollectionResume : resumeCollection) {
                resumeCollectionResume.setVacancyId(null);
                resumeCollectionResume = em.merge(resumeCollectionResume);
            }
            Collection<Interview> interviewCollection = vacancy.getInterviewCollection();
            for (Interview interviewCollectionInterview : interviewCollection) {
                interviewCollectionInterview.setVacancyId(null);
                interviewCollectionInterview = em.merge(interviewCollectionInterview);
            }
            em.remove(vacancy);
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

    public List<Vacancy> findVacancyEntities() {
        return findVacancyEntities(true, -1, -1);
    }

    public List<Vacancy> findVacancyEntities(int maxResults, int firstResult) {
        return findVacancyEntities(false, maxResults, firstResult);
    }

    private List<Vacancy> findVacancyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vacancy.class));
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

    public Vacancy findVacancy(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vacancy.class, id);
        } finally {
            em.close();
        }
    }

    public int getVacancyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vacancy> rt = cq.from(Vacancy.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
    public List<Vacancy> getVacanciesByStatus(int status) {
        EntityManager em = getEntityManager();
        try {
            Query query =
                em.createNamedQuery("Vacancy.findByStatus");
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Vacancy> getVacanciesByEmployerId(int employerId) {
        EntityManager em = getEntityManager();
        try {
//            Query query =
//                em.createNamedQuery("Employer.findById");
//            query.setParameter("id", employerId);
//            Employer e = (Employer)query.getSingleResult();
//            System.out.println("found employer with id: " + e.getId());
//            query = em.createNamedQuery("Vacancy.findByEmployer");
//            query.setParameter("employer", e);
//            List<Vacancy> list = query.getResultList();
//            System.out.println("found vacancies: " + list.size());
//            return list;

            Query query =
                em.createNamedQuery("Vacancy.findByEmployerId");
            query.setParameter("employerId", employerId);
            return query.getResultList();

        } finally {
            em.close();
        }
    }
}
