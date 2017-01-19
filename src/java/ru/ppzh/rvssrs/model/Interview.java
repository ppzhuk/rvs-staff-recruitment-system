/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ppzh.rvssrs.model;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author Nataly
 */
@Entity
@Table(name = "interview", catalog = "recruitment", schema = "")
@NamedQueries({
    @NamedQuery(name = "Interview.findAll", query = "SELECT i FROM Interview i")
    , @NamedQuery(name = "Interview.findById", query = "SELECT i FROM Interview i WHERE i.id = :id")
    , @NamedQuery(name = "Interview.findByDate", query = "SELECT i FROM Interview i WHERE i.date = :date")
    , @NamedQuery(name = "Interview.findByEmployerResult", query = "SELECT i FROM Interview i WHERE i.employerResult = :employerResult")
    , @NamedQuery(name = "Interview.findByApplicantResult", query = "SELECT i FROM Interview i WHERE i.applicantResult = :applicantResult")
    , @NamedQuery(name = "Interview.findByApplicantId", query = "SELECT i FROM Interview i WHERE i.applicantId.id = :applicantId")
    , @NamedQuery(name = "Interview.findByEmployerId", query = "SELECT i FROM Interview i WHERE i.vacancyId.employerId.id = :employerId")})
public class Interview implements Serializable {
    
    public static final int RESULT_POSITIVE = 1;
    public static final int RESULT_UNDEFINED = 0;
    public static final int RESULT_NEGATIVE = -1;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 100)
    @Column(name = "date", length = 100)
    private String date;
    @Column(name = "employer_result")
    private Integer employerResult = RESULT_UNDEFINED;
    @Column(name = "applicant_result")
    private Integer applicantResult = RESULT_UNDEFINED;
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Applicant applicantId;
    @JoinColumn(name = "vacancy_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Vacancy vacancyId;

    public Interview() {
    }

    public Interview(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getEmployerResult() {
        return employerResult;
    }

    public void setEmployerResult(Integer employerResult) {
        this.employerResult = employerResult;
    }

    public Integer getApplicantResult() {
        return applicantResult;
    }

    public void setApplicantResult(Integer applicantResult) {
        this.applicantResult = applicantResult;
    }

    public Applicant getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Applicant applicantId) {
        this.applicantId = applicantId;
    }

    public Vacancy getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Vacancy vacancyId) {
        this.vacancyId = vacancyId;
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
        if (!(object instanceof Interview)) {
            return false;
        }
        Interview other = (Interview) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.ppzh.rvssrs.model.Interview[ id=" + id + " ]";
    }
    
    public int getInterviewResult() {
        if (this.applicantResult == RESULT_POSITIVE && this.employerResult == RESULT_POSITIVE) {
            return RESULT_POSITIVE;
        } else if ((this.applicantResult == RESULT_NEGATIVE && this.employerResult == RESULT_POSITIVE) ||
                   (this.employerResult == RESULT_NEGATIVE && this.applicantResult == RESULT_POSITIVE) ||
                   (this.employerResult == RESULT_NEGATIVE && this.applicantResult == RESULT_NEGATIVE)) {
            return RESULT_NEGATIVE;
        } 
        return RESULT_UNDEFINED;
    }
    
    public boolean isInterviewPassed() {
        return date.compareTo(Vacancy.getToday()) < 1;
    }
}
