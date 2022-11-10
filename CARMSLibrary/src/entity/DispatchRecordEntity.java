/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author stonley
 */
@Entity
public class DispatchRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dispatchRecordID;
    
    @Column(nullable = false, unique = true)
    private String dispatchRecordName;
    
    //@Column(nullable = false)
    private Boolean isCompleted;
    
    //@Temporal(TemporalType.TIMESTAMP)
    //@Column(nullable = false)
    private Date pickUpTime;
    
    //@Temporal(TemporalType.TIMESTAMP)
    //@Column(nullable = false)
    private Date returnTime;
    
//    @ManyToOne
//    private OutletEntity currOutlet;
    
//    @OneToOne
//    private EmployeeEntity employee;

    public DispatchRecordEntity() {
    }
    
    public DispatchRecordEntity(Boolean isCompleted, Date pickUpTime, Date returnTime) {
        this.isCompleted = isCompleted;
        this.pickUpTime = pickUpTime;
        this.returnTime = returnTime;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Date getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(Date pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

//    public OutletEntity getCurrOutlet() {
//        return currOutlet;
//    }
//
//    public void setCurrOutlet(OutletEntity currOutlet) {
//        this.currOutlet = currOutlet;
//    }

//    public EmployeeEntity getEmployee() {
//        return employee;
//    }
//
//    public void setEmployee(EmployeeEntity employee) {
//        this.employee = employee;
//    }

    public Long getDispatchRecordID() {
        return dispatchRecordID;
    }

    public void setDispatchRecordID(Long dispatchRecordID) {
        this.dispatchRecordID = dispatchRecordID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dispatchRecordID != null ? dispatchRecordID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the dispatchRecordID fields are not set
        if (!(object instanceof DispatchRecordEntity)) {
            return false;
        }
        DispatchRecordEntity other = (DispatchRecordEntity) object;
        if ((this.dispatchRecordID == null && other.dispatchRecordID != null) || (this.dispatchRecordID != null && !this.dispatchRecordID.equals(other.dispatchRecordID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DispatchRecordEntity[ id=" + dispatchRecordID + " ]";
    }

    /**
     * @return the dispatchRecordName
     */
    public String getDispatchRecordName() {
        return dispatchRecordName;
    }

    /**
     * @param dispatchRecordName the dispatchRecordName to set
     */
    public void setDispatchRecordName(String dispatchRecordName) {
        this.dispatchRecordName = dispatchRecordName;
    }
    
}
