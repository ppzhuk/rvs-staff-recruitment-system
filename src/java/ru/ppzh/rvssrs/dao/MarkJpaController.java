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
import ru.ppzh.rvssrs.model.Mark;
import ru.ppzh.rvssrs.model.Person;

/**
 *
 * @author Nataly
 */
public class MarkJpaController implements Serializable {

    public MarkJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mark mark) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Person evaluatedPersonId = mark.getEvaluatedPersonId();
            if (evaluatedPersonId != null) {
                evaluatedPersonId = em.getReference(evaluatedPersonId.getClass(), evaluatedPersonId.getId());
                mark.setEvaluatedPersonId(evaluatedPersonId);
            }
            em.persist(mark);
            if (evaluatedPersonId != null) {
                evaluatedPersonId.getMarkCollection().add(mark);
                evaluatedPersonId = em.merge(evaluatedPersonId);
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

    public void edit(Mark mark) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mark persistentMark = em.find(Mark.class, mark.getId());
            Person evaluatedPersonIdOld = persistentMark.getEvaluatedPersonId();
            Person evaluatedPersonIdNew = mark.getEvaluatedPersonId();
            if (evaluatedPersonIdNew != null) {
                evaluatedPersonIdNew = em.getReference(evaluatedPersonIdNew.getClass(), evaluatedPersonIdNew.getId());
                mark.setEvaluatedPersonId(evaluatedPersonIdNew);
            }
            mark = em.merge(mark);
            if (evaluatedPersonIdOld != null && !evaluatedPersonIdOld.equals(evaluatedPersonIdNew)) {
                evaluatedPersonIdOld.getMarkCollection().remove(mark);
                evaluatedPersonIdOld = em.merge(evaluatedPersonIdOld);
            }
            if (evaluatedPersonIdNew != null && !evaluatedPersonIdNew.equals(evaluatedPersonIdOld)) {
                evaluatedPersonIdNew.getMarkCollection().add(mark);
                evaluatedPersonIdNew = em.merge(evaluatedPersonIdNew);
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
                Integer id = mark.getId();
                if (findMark(id) == null) {
                    throw new NonexistentEntityException("The mark with id " + id + " no longer exists.");
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
            Mark mark;
            try {
                mark = em.getReference(Mark.class, id);
                mark.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mark with id " + id + " no longer exists.", enfe);
            }
            Person evaluatedPersonId = mark.getEvaluatedPersonId();
            if (evaluatedPersonId != null) {
                evaluatedPersonId.getMarkCollection().remove(mark);
                evaluatedPersonId = em.merge(evaluatedPersonId);
            }
            em.remove(mark);
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

    public List<Mark> findMarkEntities() {
        return findMarkEntities(true, -1, -1);
    }

    public List<Mark> findMarkEntities(int maxResults, int firstResult) {
        return findMarkEntities(false, maxResults, firstResult);
    }

    private List<Mark> findMarkEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mark.class));
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

    public Mark findMark(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mark.class, id);
        } finally {
            em.close();
        }
    }

    public int getMarkCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mark> rt = cq.from(Mark.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
    public List<Mark> getMarksByManagerId(int managerId) {
        EntityManager em = getEntityManager();
        try {
            Query query =
                em.createNamedQuery("Mark.findByManagerId");
            query.setParameter("managerId", managerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
