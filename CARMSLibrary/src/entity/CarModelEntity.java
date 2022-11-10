/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author stonley
 */
@Entity
public class CarModelEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carModelID;
    
    //@NotNull
    //@Column(nullable = false, length = 64)
    private String modelName;
    
    //@NotNull
    //@Column(nullable = false, length = 64)
    private String modelMake;
    
    //@ManyToOne(optional = false)
    //@JoinColumn(nullable = false)
    @ManyToOne
    private CarCategoryEntity category;

    public CarModelEntity() {
    }

    public CarModelEntity(String modelMake, String modelName, CarCategoryEntity category) {
        this.modelName = modelName;
        this.modelMake = modelMake;
        this.category = category;
    }

    public Long getCarModelID() {
        return carModelID;
    }

    public void setCarModelID(Long carModelID) {
        this.carModelID = carModelID;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public CarCategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CarCategoryEntity category) {
        this.category = category;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carModelID != null ? carModelID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the carModelID fields are not set
        if (!(object instanceof CarModelEntity)) {
            return false;
        }
        CarModelEntity other = (CarModelEntity) object;
        if ((this.carModelID == null && other.carModelID != null) || (this.carModelID != null && !this.carModelID.equals(other.carModelID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CarModelEntity[ id=" + carModelID + " ]";
    }

    /**
     * @return the modelMake
     */
    public String getModelMake() {
        return modelMake;
    }

    /**
     * @param modelMake the modelMake to set
     */
    public void setModelMake(String modelMake) {
        this.modelMake = modelMake;
    }
    
}
