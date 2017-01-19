/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.dao;

import java.io.Serializable;
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
import ru.ppzh.rvssrs.model.Resume;
import ru.ppzh.rvssrs.model.Vacancy;

/**
 *
 * @author Nataly
 */
public class ResumeJpaController implements Serializable {

    public ResumeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Resume resume) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Applicant applicantId = resume.getApplicantId();
            if (applicantId != null) {
                applicantId = em.getReference(applicantId.getClass(), applicantId.getId());
                resume.setApplicantId(applicantId);
            }
            Vacancy vacancyId = resume.getVacancyId();
            if (vacancyId != null) {
                vacancyId = em.getReference(vacancyId.getClass(), vacancyId.getId());
                resume.setVacancyId(vacancyId);
            }
            em.persist(resume);
            if (applicantId != null) {
                applicantId.getResumeCollection().add(resume);
                applicantId = em.merge(applicantId);
            }
            if (vacancyId != null) {
                vacancyId.getResumeCollection().add(resume);
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

    public void edit(Resume resume) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Resume persistentResume = em.find(Resume.class, resume.getId());
            Applicant applicantIdOld = persistentResume.getApplicantId();
            Applicant applicantIdNew = resume.getApplicantId();
            Vacancy vacancyIdOld = persistentResume.getVacancyId();
            Vacancy vacancyIdNew = resume.getVacancyId();
            if (applicantIdNew != null) {
                applicantIdNew = em.getReference(applicantIdNew.getClass(), applicantIdNew.getId());
                resume.setApplicantId(applicantIdNew);
            }
            if (vacancyIdNew != null) {
                vacancyIdNew = em.getReference(vacancyIdNew.getClass(), vacancyIdNew.getId());
                resume.setVacancyId(vacancyIdNew);
            }
            resume = em.merge(resume);
            System.out.println("resume with id: " + resume.getId() + " merged!");
            if (applicantIdOld != null && !applicantIdOld.equals(applicantIdNew)) {
                applicantIdOld.getResumeCollection().remove(resume);
                applicantIdOld = em.merge(applicantIdOld);
            }
            if (applicantIdNew != null && !applicantIdNew.equals(applicantIdOld)) {
                applicantIdNew.getResumeCollection().add(resume);
                applicantIdNew = em.merge(applicantIdNew);
            }
            if (vacancyIdOld != null && !vacancyIdOld.equals(vacancyIdNew)) {
                vacancyIdOld.getResumeCollection().remove(resume);
                vacancyIdOld = em.merge(vacancyIdOld);
            }
            if (vacancyIdNew != null && !vacancyIdNew.equals(vacancyIdOld)) {
                System.out.println("vacancy with id: " + vacancyIdNew.getId() + " merged!");
                vacancyIdNew.getResumeCollection().add(resume);
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
                Integer id = resume.getId();
                if (findResume(id) == null) {
                    throw new NonexistentEntityException("The resume with id " + id + " no longer exists.");
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
            Resume resume;
            try {
                resume = em.getReference(Resume.class, id);
                resume.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The resume with id " + id + " no longer exists.", enfe);
            }
            Applicant applicantId = resume.getApplicantId();
            if (applicantId != null) {
                applicantId.getResumeCollection().remove(resume);
                applicantId = em.merge(applicantId);
            }
            Vacancy vacancyId = resume.getVacancyId();
            if (vacancyId != null) {
                vacancyId.getResumeCollection().remove(resume);
                vacancyId = em.merge(vacancyId);
            }
            em.remove(resume);
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

    public List<Resume> findResumeEntities() {
        return findResumeEntities(true, -1, -1);
    }

    public List<Resume> findResumeEntities(int maxResults, int firstResult) {
        return findResumeEntities(false, maxResults, firstResult);
    }

    private List<Resume> findResumeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Resume.class));
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

    public Resume findResume(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Resume.class, id);
        } finally {
            em.close();
        }
    }

    public int getResumeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Resume> rt = cq.from(Resume.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Resume> getResumesByInSearch(boolean inSearch) {
        EntityManager em = getEntityManager();
        try {
            Query query =
                em.createNamedQuery("Resume.findByInSearch");
            query.setParameter("inSearch", inSearch);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Resume> getVacanciesByApplicantId(int applicantId) {
        EntityManager em = getEntityManager();
        try {
            Query query =
                em.createNamedQuery("Resume.findByApplicantId");
            query.setParameter("applicantId", applicantId);
            return query.getResultList();

        } finally {
            em.close();
        }
    }
}
