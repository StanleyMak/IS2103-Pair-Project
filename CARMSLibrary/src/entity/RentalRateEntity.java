/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author stonley
 */
@Entity
public class RentalRateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalRateID;
    private double rate;
    private Date startDate;
    private Date endDate;
    
    private CarCategoryEntity category;
    
    private CarEntity car;

    public Long getRentalRateID() {
        return rentalRateID;
    }

    public void setRentalRateID(Long rentalRateID) {
        this.rentalRateID = rentalRateID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rentalRateID != null ? rentalRateID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the rentalRateID fields are not set
        if (!(object instanceof RentalRateEntity)) {
            return false;
        }
        RentalRateEntity other = (RentalRateEntity) object;
        if ((this.rentalRateID == null && other.rentalRateID != null) || (this.rentalRateID != null && !this.rentalRateID.equals(other.rentalRateID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RentalRateEntity[ id=" + rentalRateID + " ]";
    }
    
}
