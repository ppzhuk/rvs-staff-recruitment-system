/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import ru.ppzh.rvssrs.dao.exceptions.NonexistentEntityException;
import ru.ppzh.rvssrs.dao.exceptions.RollbackFailureException;
import ru.ppzh.rvssrs.model.Applicant;
import ru.ppzh.rvssrs.model.Interview;
import ru.ppzh.rvssrs.model.Vacancy;

/**
 *
 * @author Nataly
 */
public class InterviewJpaController implements Serializable {

    public InterviewJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Interview interview) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Applicant applicantId = interview.getApplicantId();
            if (applicantId != null) {
                applicantId = em.getReference(applicantId.getClass(), applicantId.getId());
                interview.setApplicantId(applicantId);
            }
            Vacancy vacancyId = interview.getVacancyId();
            if (vacancyId != null) {
                vacancyId = em.getReference(vacancyId.getClass(), vacancyId.getId());
                interview.setVacancyId(vacancyId);
            }
            em.persist(interview);
            if (applicantId != null) {
                applicantId.getInterviewCollection().add(interview);
                applicantId = em.merge(applicantId);
            }
            if (vacancyId != null) {
                vacancyId.getInterviewCollection().add(interview);
                vacancyId = em.merge(vacancyId);
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

    public void edit(Interview interview) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Interview persistentInterview = em.find(Interview.class, interview.getId());
            Applicant applicantIdOld = persistentInterview.getApplicantId();
            Applicant applicantIdNew = interview.getApplicantId();
            Vacancy vacancyIdOld = persistentInterview.getVacancyId();
            Vacancy vacancyIdNew = interview.getVacancyId();
            if (applicantIdNew != null) {
                applicantIdNew = em.getReference(applicantIdNew.getClass(), applicantIdNew.getId());
                interview.setApplicantId(applicantIdNew);
            }
            if (vacancyIdNew != null) {
                vacancyIdNew = em.getReference(vacancyIdNew.getClass(), vacancyIdNew.getId());
                interview.setVacancyId(vacancyIdNew);
            }
            interview = em.merge(interview);
            if (applicantIdOld != null && !applicantIdOld.equals(applicantIdNew)) {
                applicantIdOld.getInterviewCollection().remove(interview);
                applicantIdOld = em.merge(applicantIdOld);
            }
            if (applicantIdNew != null && !applicantIdNew.equals(applicantIdOld)) {
                applicantIdNew.getInterviewCollection().add(interview);
                applicantIdNew = em.merge(applicantIdNew);
            }
            if (vacancyIdOld != null && !vacancyIdOld.equals(vacancyIdNew)) {
                vacancyIdOld.getInterviewCollection().remove(interview);
                vacancyIdOld = em.merge(vacancyIdOld);
            }
            if (vacancyIdNew != null && !vacancyIdNew.equals(vacancyIdOld)) {
                vacancyIdNew.getInterviewCollection().add(interview);
                vacancyIdNew = em.merge(vacancyIdNew);
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
                Integer id = interview.getId();
                if (findInterview(id) == null) {
                    throw new NonexistentEntityException("The interview with id " + id + " no longer exists.");
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
            Interview interview;
            try {
                interview = em.getReference(Interview.class, id);
                interview.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The interview with id " + id + " no longer exists.", enfe);
            }
            Applicant applicantId = interview.getApplicantId();
            if (applicantId != null) {
                applicantId.getInterviewCollection().remove(interview);
                applicantId = em.merge(applicantId);
            }
            Vacancy vacancyId = interview.getVacancyId();
            if (vacancyId != null) {
                vacancyId.getInterviewCollection().remove(interview);
                vacancyId = em.merge(vacancyId);
            }
            em.remove(interview);
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

    public List<Interview> findInterviewEntities() {
        return findInterviewEntities(true, -1, -1);
    }

    public List<Interview> findInterviewEntities(int maxResults, int firstResult) {
        return findInterviewEntities(false, maxResults, firstResult);
    }

    private List<Interview> findInterviewEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Interview.class));
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

    public Interview findInterview(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Interview.class, id);
        } finally {
            em.close();
        }
    }

    public int getInterviewCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Interview> rt = cq.from(Interview.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Interview> getFutureInterviews() {
        List<Interview> list = findInterviewEntities();
        List<Interview> futureInterviews = new ArrayList<>();
        for (Interview i: list) {
            if (!i.isInterviewPassed()) {
                futureInterviews.add(i);
            }
        }
        return futureInterviews;
    }
    
    public List<Interview> getPassedInterviews() {
        List<Interview> list = findInterviewEntities();
        List<Interview> futureInterviews = new ArrayList<>();
        for (Interview i: list) {
            if (i.isInterviewPassed()) {
                futureInterviews.add(i);
            }
        }
        return futureInterviews;
    }
    
    public List<Interview> getInterviewsByApplicantId(int applicantId) {
        EntityManager em = getEntityManager();
        try {
            Query query =
                em.createNamedQuery("Interview.findByApplicantId");
            query.setParameter("applicantId", applicantId);
            return query.getResultList();

        } finally {
            em.close();
        }
    }
    
    public List<Interview> getInterviewsByEmployerId(int employerId) {
        EntityManager em = getEntityManager();
        try {
            Query query =
                em.createNamedQuery("Interview.findByEmployerId");
            query.setParameter("employerId", employerId);
            return query.getResultList();

        } finally {
            em.close();
        }
    }
}
