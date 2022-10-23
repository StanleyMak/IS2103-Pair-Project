/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
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
public class OutletEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outletID;
    private String address;
    private Date openHour;
    private Date closeHour;
    
    private List<CarEntity> cars;
    
    private List<EmployeeEntity> employees;
    
    private List<DispatchRecordEntity> dispatchRecords;
    
    
    
    
    

    public Long getOutletID() {
        return outletID;
    }

    public void setOutletID(Long outletID) {
        this.outletID = outletID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (outletID != null ? outletID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the outletID fields are not set
        if (!(object instanceof OutletEntity)) {
            return false;
        }
        OutletEntity other = (OutletEntity) object;
        if ((this.outletID == null && other.outletID != null) || (this.outletID != null && !this.outletID.equals(other.outletID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OutletEntity[ id=" + outletID + " ]";
    }
    
}
