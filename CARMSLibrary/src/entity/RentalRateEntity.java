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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import util.enumeration.RentalRateTypeEnum;

/**
 *
 * @author stonley
 */
@Entity
public class RentalRateEntity implements Serializable, Comparable<RentalRateEntity> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalRateID;
    
    //@NotNull
    //@Column(nullable = false)
    private String rentalName;
    
    //@NotNull
    @Enumerated(EnumType.STRING)
    private RentalRateTypeEnum rentalRateType;
    
    //@NotNull
    //@Column(nullable = false)
    private double ratePerDay;
    
    //@Column(nullable = false, length = 64)
    private Date startDate;
    //@Column(nullable = false, length = 64)
    private Date endDate;
    
    private Boolean isDisabled;
    
    //@ManyToOne(optional = false)
    //@JoinColumn(nullable = false)
    @ManyToOne
    private CarCategoryEntity carCategory;

    public RentalRateEntity() {
        this.isDisabled = false;
    }

    public RentalRateEntity(String rentalName, RentalRateTypeEnum rentalRateType, CarCategoryEntity carCategory, double ratePerDay, Date startDate, Date endDate) {
        this();
        this.rentalName = rentalName;
        this.ratePerDay = ratePerDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.carCategory = carCategory;
        this.rentalRateType = rentalRateType;
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

    
    //REVIEW!!!!
    @Override
    public int compareTo(RentalRateEntity o) {

        if (this.rentalName.compareTo(o.getRentalName()) == 0) {
            return 0;
        } else if (this.rentalName.compareTo(o.getRentalName()) > 0) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * @return the carCategory
     */
    public CarCategoryEntity getCarCategory() {
        return carCategory;
    }

    /**
     * @param carCategory the carCategory to set
     */
    public void setCarCategory(CarCategoryEntity carCategory) {
        this.carCategory = carCategory;
    }

    /**
     * @return the rentalRateType
     */
    public RentalRateTypeEnum getRentalRateType() {
        return rentalRateType;
    }

    /**
     * @param rentalRateType the rentalRateType to set
     */
    public void setRentalRateType(RentalRateTypeEnum rentalRateType) {
        this.rentalRateType = rentalRateType;
    }

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

}
