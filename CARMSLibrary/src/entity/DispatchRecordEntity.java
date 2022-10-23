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
public class DispatchRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dispatchRecordID;
    private Boolean isCompleted;
    private Date pickUpTime;
    private Date returnTime;
    
    private ReservationEntity reservation;
    
    private CarEntity car;
    
    private OutletEntity currOutlet;
    
    private OutletEntity returnOutlet;
    
    private EmployeeEntity employee;

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
    
}
