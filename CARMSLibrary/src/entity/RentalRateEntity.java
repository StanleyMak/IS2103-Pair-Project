/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
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
    @Column(nullable = false, length = 64)
    private String rentalName; 
    @Column(nullable = false, length = 64)
    private double ratePerDay;
    @Column(nullable = false, length = 64)
    private Date startDate;
    @Column(nullable = false, length = 64)
    private Date endDate;

    public RentalRateEntity() {
    }

    public RentalRateEntity(String rentalName, double ratePerDay, Date startDate, Date endDate) {
        this.rentalName = rentalName;
        this.ratePerDay = ratePerDay;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getRentalRateID() {
        return rentalRateID;
    }

    public void setRentalRateID(Long rentalRateID) {
        this.rentalRateID = rentalRateID;
    }

    public String getRentalName() {
        return rentalName;
    }

    public void setRentalName(String rentalName) {
        this.rentalName = rentalName;
    }

    public double getRatePerDay() {
        return ratePerDay;
    }

    public void setRatePerDay(double ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
