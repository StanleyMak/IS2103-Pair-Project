/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author stonley
 */
@Entity
public class OutletEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outletID;
    
    @NotNull
    @Column(nullable = false)
    @Size(min = 5)
    private String address;
    
    @Temporal(TemporalType.TIME)
    private Date openHour;
    
    @Temporal(TemporalType.TIME)
    private Date closeHour;

    public OutletEntity() {
    }

    public OutletEntity(String address, Date openHour, Date closeHour) {
        this();
        this.address = address;
        try {
            if (openHour == null) {
                this.openHour = timeFormat.parse("00:00");
            } else {
                this.openHour = openHour;
            }
            if (closeHour == null) {
                this.closeHour = timeFormat.parse("23:59");
            } else {
                this.closeHour = closeHour;
            }
        } catch (ParseException e) {
            System.out.println("Invalid Time Format!");
        }
        
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
