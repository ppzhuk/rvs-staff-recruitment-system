/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Nataly
 */
@Entity
@Table(name = "applicant", catalog = "recruitment", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"person_id"})})
@NamedQueries({
    @NamedQuery(name = "Applicant.findAll", query = "SELECT a FROM Applicant a")
    , @NamedQuery(name = "Applicant.findById", query = "SELECT a FROM Applicant a WHERE a.id = :id")})
public class Applicant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "applicantId", fetch = FetchType.EAGER)
    private Collection<Resume> resumeCollection;
    @OneToMany(mappedBy = "applicantId", fetch = FetchType.EAGER)
    private Collection<Interview> interviewCollection;
    @OneToMany(mappedBy = "applicantId", fetch = FetchType.EAGER)
    private Collection<Vacancy> vacancyCollection;
    @JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private Person personId;

    public Applicant() {
    }

    public Applicant(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Collection<Resume> getResumeCollection() {
        return resumeCollection;
    }

    public void setResumeCollection(Collection<Resume> resumeCollection) {
        this.resumeCollection = resumeCollection;
    }

    public Collection<Interview> getInterviewCollection() {
        return interviewCollection;
    }

    public void setInterviewCollection(Collection<Interview> interviewCollection) {
        this.interviewCollection = interviewCollection;
    }

    public Collection<Vacancy> getVacancyCollection() {
        return vacancyCollection;
    }

    public void setVacancyCollection(Collection<Vacancy> vacancyCollection) {
        this.vacancyCollection = vacancyCollection;
    }

    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(Person personId) {
        this.personId = personId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Applicant)) {
            return false;
        }
        Applicant other = (Applicant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[ id= " + id + ", name= " + this.personId.getName() + ", inSearch= " + this.resumeCollection.iterator().next().getInSearch() + " ]";
    }
    
    public Resume getResume() {
        return this.resumeCollection.iterator().next();
    }
}
