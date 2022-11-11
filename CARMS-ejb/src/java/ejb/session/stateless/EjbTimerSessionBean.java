/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.CarModelEntity;
import entity.DispatchRecordEntity;
import entity.OutletEntity;
import entity.ReservationEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import util.enumeration.StatusEnum;
import util.exception.DispatchRecordNameExistsException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author stonley
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "DispatchRecordSessionBeanLocal")
    private DispatchRecordSessionBeanLocal dispatchRecordSessionBeanLocal;

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBeanLocal;

    //2 hours as it takes 2 hours to dispatch a car
    @Schedule(hour = "2", info = "allocateCarsCurrentDayReservation")
    public void allocateCarsCurrentDayReservation() {
        String currDate = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println("********** Car Allocation For Current Day Reservation: Timeout at " + currDate);
        
        //!!need to retrieve all records for current day!!
        List<ReservationEntity> reservations = reservationSessionBeanLocal.retrieveAllReservations(); 
        
        for (ReservationEntity reservation : reservations) {
            CarModelEntity carModel = reservation.getCarModel();
            CarCategoryEntity carCategory = reservation.getCarCategory();
            List<CarEntity> cars = new ArrayList<>();
            if (carModel == null) {
                cars = carSessionBeanLocal.retrieveAllCarsOfCarCategory(carCategory.getCategoryName());
            } else {
                cars = carSessionBeanLocal.retrieveAllCarsOfCarModel(carModel.getModelName());
            }
            OutletEntity pickupOutlet = reservation.getPickUpOutlet();
            boolean isAllocated = false;
            for (CarEntity car : cars) {
                if (pickupOutlet.getOutletID() == car.getCurrOutlet().getOutletID()) {
                    if (true/*car is available*/) {
                        try {
                            carSessionBeanLocal.allocateCarToReservation(car.getCarID(), reservation.getReservationID());
                            System.out.println("********** Car " + car.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + reservation.getStartDateTime() + "!\n");
                            isAllocated = true;
                            break;
                        } catch (ReservationNotFoundException e) {
                            System.out.println("Error: " + e.getMessage() + "!\n");
                        }
                    }
                }
            }
            
            if (!isAllocated) {
                for (CarEntity car : cars) {
                    if (car.getStatus() == StatusEnum.AVAILABLE) {
                        try {
                            carSessionBeanLocal.allocateCarToReservation(car.getCarID(), reservation.getReservationID());
                            System.out.println("********** Car " + car.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + reservation.getStartDateTime() + "!\n");
                            System.out.println("********** Car <LicensePlateNumber> at Outlet <OutletAddress> requires transit dispatch!");
                            break;
                        } catch (ReservationNotFoundException e) {
                            System.out.println("Error: " + e.getMessage() + "!\n");
                        }
                    }
                }
            }
        }
    }

    @Schedule(hour = "2", minute = "1", info = "generateDispatchRecordCurrentDayReservation")
    public void generateDispatchRecordCurrentDayReservation() {
        String currDate = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println("********** Dispatch Record Generation For Current Day Reservation: Timeout at " + currDate);

        //!!need to retrieve all records for current day!!
        List<ReservationEntity> reservations = reservationSessionBeanLocal.retrieveAllReservations(); 
        
        for (ReservationEntity reservation : reservations) {
            if (reservation.getCar().getCurrOutlet().getOutletID() == reservation.getPickUpOutlet().getOutletID()) {
                try {
                    DispatchRecordEntity dispatchRecord = new DispatchRecordEntity();
                    //associate record with outlets, dispatchRecord.setOutlet()
                    
                    Date dispatchTime = reservation.getStartDateTime();
                    dispatchTime.setHours(reservation.getStartDateTime().getHours() - 2);
                    dispatchRecord.setPickUpTime(dispatchTime);
                    
                    dispatchRecordSessionBeanLocal.createNewDispatchRecord(dispatchRecord);
                    System.out.println("********** New Transit Dispatch Record created for Car <LicensePlateNumber>!");
                } catch (DispatchRecordNameExistsException e) {
                    System.out.println("Error: " + e.getMessage() + "!\n");
                }
            }
        }
    }

}
