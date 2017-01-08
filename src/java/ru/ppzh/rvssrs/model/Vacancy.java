/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Nataly
 */
@Entity
@Table(name = "vacancy", catalog = "recruitment", schema = "")
@NamedQueries({
    @NamedQuery(name = "Vacancy.findAll", query = "SELECT v FROM Vacancy v")
    , @NamedQuery(name = "Vacancy.findById", query = "SELECT v FROM Vacancy v WHERE v.id = :id")
    , @NamedQuery(name = "Vacancy.findByPosition", query = "SELECT v FROM Vacancy v WHERE v.position = :position")
    , @NamedQuery(name = "Vacancy.findByRequirments", query = "SELECT v FROM Vacancy v WHERE v.requirments = :requirments")
    , @NamedQuery(name = "Vacancy.findBySalary", query = "SELECT v FROM Vacancy v WHERE v.salary = :salary")
    , @NamedQuery(name = "Vacancy.findByStatus", query = "SELECT v FROM Vacancy v WHERE v.status = :status")
    , @NamedQuery(name = "Vacancy.findByCloseDate", query = "SELECT v FROM Vacancy v WHERE v.closeDate = :closeDate")})
public class Vacancy implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "position", nullable = false, length = 45)
    private String position;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "requirments", nullable = false, length = 500)
    private String requirments;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "salary", precision = 15, scale = 2)
    private BigDecimal salary;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status", nullable = false)
    private int status;
    @Size(max = 100)
    @Column(name = "closeDate", length = 100)
    private String closeDate;
    @OneToMany(mappedBy = "vacancyId", fetch = FetchType.EAGER)
    private Collection<Resume> resumeCollection;
    @OneToMany(mappedBy = "vacancyId", fetch = FetchType.EAGER)
    private Collection<Interview> interviewCollection;
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Applicant applicantId;
    @JoinColumn(name = "employer_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Employer employerId;

    public Vacancy() {
    }

    public Vacancy(Integer id) {
        this.id = id;
    }

    public Vacancy(Integer id, String position, String requirments, int status) {
        this.id = id;
        this.position = position;
        this.requirments = requirments;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRequirments() {
        return requirments;
    }

    public void setRequirments(String requirments) {
        this.requirments = requirments;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
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

    public Applicant getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Applicant applicantId) {
        this.applicantId = applicantId;
    }

    public Employer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Employer employerId) {
        this.employerId = employerId;
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
        if (!(object instanceof Vacancy)) {
            return false;
        }
        Vacancy other = (Vacancy) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.ppzh.rvssrs.model.Vacancy[ id=" + id + " ]";
    }
    
}
