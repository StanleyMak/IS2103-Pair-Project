/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 *
 * @author hanyang
 */
@Entity
public class CarCategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carCategoryID;
    
    //@NotNull
    @Column(nullable = false, length = 64)
    private String categoryName;

    public CarCategoryEntity() {
    }

    public CarCategoryEntity(String categoryName) {
        this();
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCarCategoryID() {
        return carCategoryID;
    }

    public void setCarCategoryID(Long carCategoryID) {
        this.carCategoryID = carCategoryID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carCategoryID != null ? carCategoryID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the carCategoryID fields are not set
        if (!(object instanceof CarCategoryEntity)) {
            return false;
        }
        CarCategoryEntity other = (CarCategoryEntity) object;
        if ((this.carCategoryID == null && other.carCategoryID != null) || (this.carCategoryID != null && !this.carCategoryID.equals(other.carCategoryID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CarCategoryEntity[ id=" + carCategoryID + " ]";
    }

}
