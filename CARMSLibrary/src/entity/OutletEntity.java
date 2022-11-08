/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
    @Column(nullable = false, length = 64)
    private String address;
    //@Column(nullable = false, length = 64)
    private Date openHour;
    //@Column(nullable = false, length = 64)
    private Date closeHour;
    
    @OneToMany
    private List<CarEntity> cars;

    public OutletEntity() {
        this.cars = new ArrayList<>(); 
    }

    public OutletEntity(String address, Date openHour, Date closeHour) {
        this();
        this.address = address;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }
    
    public Long getOutletID() {
        return outletID;
    }

    public void setOutletID(Long outletID) {
        this.outletID = outletID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getOpenHour() {
        return openHour;
    }

    public void setOpenHour(Date openHour) {
        this.openHour = openHour;
    }

    public Date getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(Date closeHour) {
        this.closeHour = closeHour;
    }

    public List<CarEntity> getCars() {
        return cars;
    }

    public void setCars(List<CarEntity> cars) {
        this.cars = cars;
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
