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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Nataly
 */
@Entity
@Table(name = "employer", catalog = "recruitment", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"person_id"})})
@NamedQueries({
    @NamedQuery(name = "Employer.findAll", query = "SELECT e FROM Employer e")
    , @NamedQuery(name = "Employer.findById", query = "SELECT e FROM Employer e WHERE e.id = :id")
    , @NamedQuery(name = "Employer.findByCompanyName", query = "SELECT e FROM Employer e WHERE e.companyName = :companyName")
    , @NamedQuery(name = "Employer.findByDescription", query = "SELECT e FROM Employer e WHERE e.description = :description")
    , @NamedQuery(name = "Employer.findBySite", query = "SELECT e FROM Employer e WHERE e.site = :site")})
public class Employer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "company_name", nullable = false, length = 45)
    private String companyName;
    @Size(max = 45)
    @Column(name = "description", length = 45)
    private String description;
    @Size(max = 45)
    @Column(name = "site", length = 45)
    private String site;
    @JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private Person personId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employerId", fetch = FetchType.EAGER)
    private Collection<Vacancy> vacancyCollection;

    public Employer() {
    }

    public Employer(Integer id) {
        this.id = id;
    }

    public Employer(Integer id, String companyName) {
        this.id = id;
        this.companyName = companyName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(Person personId) {
        this.personId = personId;
    }

    public Collection<Vacancy> getVacancyCollection() {
        return vacancyCollection;
    }

    public void setVacancyCollection(Collection<Vacancy> vacancyCollection) {
        this.vacancyCollection = vacancyCollection;
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
        if (!(object instanceof Employer)) {
            return false;
        }
        Employer other = (Employer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.ppzh.rvssrs.model.Employer[ id=" + id + " ]";
    }
    
}
