/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import util.enumeration.StatusEnum;

/**
 *
 * @author stonley
 */
@Entity
public class CarEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carID;
    private String licensePlateNumber;
    private String colour;
    @Enumerated (EnumType.STRING)
    private StatusEnum status;
    
    private CarModelEntity model;
    
    private CarCategoryEntity category;
    
    private OutletEntity currOutlet;
    
    private DispatchRecordEntity dispatchRecord;
    
    private RentalRateEntity rentalRate;

    public Long getCarID() {
        return carID;
    }

    public void setCarID(Long carID) {
        this.carID = carID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carID != null ? carID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the carID fields are not set
        if (!(object instanceof CarEntity)) {
            return false;
        }
        CarEntity other = (CarEntity) object;
        if ((this.carID == null && other.carID != null) || (this.carID != null && !this.carID.equals(other.carID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CarEntity[ id=" + carID + " ]";
    }
    
}
