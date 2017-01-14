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
@Table(name = "mark", catalog = "recruitment", schema = "")
@NamedQueries({
    @NamedQuery(name = "Mark.findAll", query = "SELECT m FROM Mark m")
    , @NamedQuery(name = "Mark.findById", query = "SELECT m FROM Mark m WHERE m.id = :id")
    , @NamedQuery(name = "Mark.findByManagerId", query = "SELECT m FROM Mark m WHERE m.managerId = :managerId")
    , @NamedQuery(name = "Mark.findByMark", query = "SELECT m FROM Mark m WHERE m.mark = :mark")
    , @NamedQuery(name = "Mark.findByComment", query = "SELECT m FROM Mark m WHERE m.comment = :comment")})
public class Mark implements Serializable {
    public static final double SATISFACTORY_MARK = 3.0;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "manager_id", nullable = false)
    private int managerId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mark", nullable = false)
    private int mark;
    @Size(max = 45)
    @Column(name = "comment", length = 45)
    private String comment;
    @JoinColumn(name = "evaluated_person_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Person evaluatedPersonId;

    public Mark() {
    }

    public Mark(Integer id) {
        this.id = id;
    }

    public Mark(Integer id, int managerId, int mark) {
        this.id = id;
        this.managerId = managerId;
        this.mark = mark;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Person getEvaluatedPersonId() {
        return evaluatedPersonId;
    }

    public void setEvaluatedPersonId(Person evaluatedPersonId) {
        this.evaluatedPersonId = evaluatedPersonId;
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
        if (!(object instanceof Mark)) {
            return false;
        }
        Mark other = (Mark) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.ppzh.rvssrs.model.Mark[ id=" + id + " ]";
    }
    
}
