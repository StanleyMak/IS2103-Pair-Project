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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author hanyang
 */
@Entity
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationID;
    
    //@NotNull
    //@Column(nullable = false)
    private String reservationCode;
    
    //@Column(nullable = false)
    private double rentalFee; 
    
    //@Column(nullable = false, length = 16)
    //@Size(min = 16, max = 16)
    private String creditCardNumber; 
    
    //@Temporal(TemporalType.TIMESTAMP)
    //@NotNull
    //@Column(nullable = false)
    private Date startDateTime; 
    
    //@Temporal(TemporalType.TIMESTAMP)
    //@NotNull
    //@Column(nullable = false)
    private Date endDateTime; 
    
    //@Column(nullable = false)
    //@Column(nullable = false)
    private boolean onlinePayment;
    
    //@NotNull
    @ManyToOne(/*optional = false*/)
    //@JoinColumn(nullable = false)
    private CarEntity car; 
    
    @ManyToOne
    private PartnerEntity partner; 
    
    //@NotNull
    //@OneToOne(optional = false)
    //@JoinColumn(nullable = false)
    @OneToOne
    private OutletEntity pickUpOutlet;
    
    //@NotNull
    //@OneToOne(optional = false)
    //@JoinColumn(nullable = false)
    @OneToOne
    private OutletEntity returnOutlet;
    
    @OneToOne
    private CarCategoryEntity carCategory;
    
    @OneToOne
    private CarModelEntity carModel;
    
    @ManyToMany
    private List<RentalRateEntity> rentalRates;
    

    public ReservationEntity() {
        this.rentalRates = new ArrayList<>();
    }

    public ReservationEntity(double rentalFee, String creditCardNumber, Date startDateTime, Date endDateTime, String reservationCode, boolean onlinePayment) {
        this();
        this.rentalFee = rentalFee;
        this.creditCardNumber = creditCardNumber;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reservationCode = reservationCode;
        this.onlinePayment = onlinePayment;
    }

    public boolean isOnlinePayment() {
        return onlinePayment;
    }

    public void setOnlinePayment(boolean onlinePayment) {
        this.onlinePayment = onlinePayment;
    }

    public CarEntity getCar() {
        return car;
    }

    public void setCar(CarEntity car) {
        this.car = car;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public OutletEntity getPickUpOutlet() {
        return pickUpOutlet;
    }

    public void setPickUpOutlet(OutletEntity pickUpOutlet) {
        this.pickUpOutlet = pickUpOutlet;
    }

    public OutletEntity getReturnOutlet() {
        return returnOutlet;
    }

    public void setReturnOutlet(OutletEntity returnOutlet) {
        this.returnOutlet = returnOutlet;
    }
    
    public Long getReservationID() {
        return reservationID;
    }

    public void setReservationID(Long reservationID) {
        this.reservationID = reservationID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationID != null ? reservationID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationID fields are not set
        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.reservationID == null && other.reservationID != null) || (this.reservationID != null && !this.reservationID.equals(other.reservationID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationEntity[ id=" + reservationID + " ]";
    }

    public CarCategoryEntity getCarCategory() {
        return carCategory;
    }

    public void setCarCategory(CarCategoryEntity carCategory) {
        this.carCategory = carCategory;
    }

    public CarModelEntity getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModelEntity carModel) {
        this.carModel = carModel;
    }

    public List<RentalRateEntity> getRentalRates() {
        return rentalRates;
    }

    public void setRentalRates(List<RentalRateEntity> rentalRates) {
        this.rentalRates = rentalRates;
    }

    
}
