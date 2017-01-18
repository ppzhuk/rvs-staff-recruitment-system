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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Nataly
 */
@Entity
@Table(name = "resume", catalog = "recruitment", schema = "")
@NamedQueries({
    @NamedQuery(name = "Resume.findAll", query = "SELECT r FROM Resume r")
    , @NamedQuery(name = "Resume.findById", query = "SELECT r FROM Resume r WHERE r.id = :id")
    , @NamedQuery(name = "Resume.findByExperience", query = "SELECT r FROM Resume r WHERE r.experience = :experience")
    , @NamedQuery(name = "Resume.findBySkills", query = "SELECT r FROM Resume r WHERE r.skills = :skills")
    , @NamedQuery(name = "Resume.findByEducation", query = "SELECT r FROM Resume r WHERE r.education = :education")
    , @NamedQuery(name = "Resume.findByDescription", query = "SELECT r FROM Resume r WHERE r.description = :description")
    , @NamedQuery(name = "Resume.findByInSearch", query = "SELECT r FROM Resume r WHERE r.inSearch = :inSearch")
    , @NamedQuery(name = "Resume.findByApplicantId", query = "SELECT r FROM Resume r WHERE r.applicantId.id = :applicantId")})
public class Resume implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "experience", nullable = false, length = 500)
    private String experience;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "skills", nullable = false, length = 500)
    private String skills;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "education", nullable = false, length = 500)
    private String education;
    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "in_search")
    private Boolean inSearch = true;
    @JoinColumn(name = "applicant_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Applicant applicantId;
    @JoinColumn(name = "vacancy_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Vacancy vacancyId;

    public Resume() {
    }

    public Resume(Integer id) {
        this.id = id;
    }

    public Resume(Integer id, String experience, String skills, String education) {
        this.id = id;
        this.experience = experience;
        this.skills = skills;
        this.education = education;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getInSearch() {
        return inSearch;
    }

    public void setInSearch(Boolean inSearch) {
        this.inSearch = inSearch;
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
        if (!(object instanceof Resume)) {
            return false;
        }
        Resume other = (Resume) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.ppzh.rvssrs.model.Resume[ id=" + id + " ]";
    }
    
}
