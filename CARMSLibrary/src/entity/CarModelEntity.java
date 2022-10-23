/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
    private String model;
    
    private List<CarEntity> cars;
    
    private CarCategoryEntity category;

    public Long getCarModelID() {
        return carModelID;
    }

    public void setCarModelID(Long carModelID) {
        this.carModelID = carModelID;
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
    
}
