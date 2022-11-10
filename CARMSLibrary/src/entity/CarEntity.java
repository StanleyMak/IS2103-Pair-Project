/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    
    //@NotNull
    @Column(nullable = false, length = 64)
    private String licensePlateNumber;
    
    //@NotNull
    //@Column(nullable = false, length = 32)
    private String colour;
    
    //@NotNull
    @Column(nullable = false)
    @Enumerated (EnumType.STRING)
    private StatusEnum status;
    
    //@NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CarModelEntity model;
    
    //@NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private OutletEntity currOutlet;

    public CarEntity() {
    }

    public CarEntity(String licensePlateNumber, CarModelEntity model, StatusEnum status, OutletEntity currOutlet) {
        this.licensePlateNumber = licensePlateNumber;
        this.status = status;
        this.model = model;
        this.currOutlet = currOutlet;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public CarModelEntity getModel() {
        return model;
    }

    public void setModel(CarModelEntity model) {
        this.model = model;
    }

    public OutletEntity getCurrOutlet() {
        return currOutlet;
    }

    public void setCurrOutlet(OutletEntity currOutlet) {
        this.currOutlet = currOutlet;
    }
    

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
