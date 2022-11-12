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

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    //2 hours as it takes 2 hours to dispatch a car
    @Schedule(hour = "2", info = "allocateCarsCurrentDayReservation")
    public void allocateCarsCurrentDayReservation() {
        String currDate = dateTimeFormat.format(new Date());
        System.out.println("********** Car Allocation For Current Day Reservation: Timeout at " + currDate);

        //!!need to retrieve all records for current day!!
        List<ReservationEntity> reservations = reservationSessionBeanLocal.retrieveReservationsForCurrentDay(new Date());
        
        try {
            for (ReservationEntity reservation : reservations) {
                CarCategoryEntity carCategory = reservation.getCarCategory();
                OutletEntity pickupOutlet = reservation.getPickUpOutlet();

                // Assigning cars of the same outlet
                CarEntity carOfCategoryOfOutlet = carSessionBeanLocal.retrievePotentialCarOfCategoryOfOutlet(carCategory.getCategoryName(), pickupOutlet.getAddress(), reservation.getStartDateTime(), reservation.getEndDateTime());
                
                if (carOfCategoryOfOutlet != null) {
                    carSessionBeanLocal.allocateCarToReservation(carOfCategoryOfOutlet.getCarID(), reservation.getReservationID());
                    System.out.println("********** Car " + carOfCategoryOfOutlet.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + reservation.getStartDateTime() + "!\n");
                    break;
                }

                //Assigning cars of other outlet
                // setting the new start timing
                int transitWindow = reservation.getStartDateTime().getHours() - 2;
                Date newStartTime = new Date();
                newStartTime.setTime(reservation.getStartDateTime().getTime());
                newStartTime.setHours(transitWindow);
                CarEntity carOfCategoryOfOtherOutlet = carSessionBeanLocal.retrievePotentialCarOfCategoryOfOtherOutlet(carCategory.getCategoryName(), pickupOutlet.getAddress(), newStartTime, reservation.getEndDateTime());
                
                carSessionBeanLocal.allocateCarToReservation(carOfCategoryOfOtherOutlet.getCarID(), reservation.getReservationID());
                System.out.println("********** Car " + carOfCategoryOfOtherOutlet.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + dateTimeFormat.format(reservation.getStartDateTime()) + "!\n");
                System.out.println("********** Car <LicensePlateNumber> at Outlet <OutletAddress> requires transit dispatch!");
                
                generateDispatchRecordCurrentDayReservation(reservation);
            }
        } catch (ReservationNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

//        for (ReservationEntity reservation : reservations) {
//            CarCategoryEntity carCategory = reservation.getCarCategory();
//            List<CarEntity> cars = carSessionBeanLocal.retrieveAllCarsOfCarCategory(carCategory.getCategoryName());
//            OutletEntity pickupOutlet = reservation.getPickUpOutlet();
//            boolean isAllocated = false;
//
//            //assign car from pickup outlet
//            for (CarEntity car : cars) {
//                if (pickupOutlet.getOutletID() == car.getCurrOutlet().getOutletID()) {
//                    if (true/*car is available*/) {
//                        carSessionBeanLocal.allocateCarToReservation(car.getCarID(), reservation.getReservationID());
//                        System.out.println("********** Car " + car.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + reservation.getStartDateTime() + "!\n");
//                        isAllocated = true;
//                        break;
//                    }
//                }
//            }
//
//            //assign car from other outlets
//            if (!isAllocated) {
//                for (CarEntity car : cars) {
//                    if (car.getStatus() == StatusEnum.AVAILABLE) {
//                        try {
//                            carSessionBeanLocal.allocateCarToReservation(car.getCarID(), reservation.getReservationID());
//                            System.out.println("********** Car " + car.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + reservation.getStartDateTime() + "!\n");
//                            System.out.println("********** Car <LicensePlateNumber> at Outlet <OutletAddress> requires transit dispatch!");
//                            break;
//                        } catch (ReservationNotFoundException e) {
//                            System.out.println("Error: " + e.getMessage() + "!\n");
//                        }
//                    }
//                }
//            }
//        }
        

//        // generate transit dispatch record
//        TransitDispatchRecord newDispatch = new TransitDispatchRecord();
//        newDispatch.setCurrentOutlet(r.getCar().getOutlet());
//        newDispatch.setDestinationOutlet(r.getPickUpOutlet());
//
//        Date dispatchTime = r.getReservationStartDate();
//        dispatchTime.setHours(r.getReservationStartDate().getHours() - 2);
//        newDispatch.setDispatchTime(dispatchTime);
//
//        transitDispatchRecordSessionBeanLocal.createNewTransitDispatchRecord(newDispatch);
//        System.out.println("New Transit Dispatch Record Created for " + r.getCar().getLicensePlate());
//        break;
    }

    private void generateDispatchRecordCurrentDayReservation(ReservationEntity reservation) {
        String currDate = dateTimeFormat.format(new Date());
        System.out.println("********** Dispatch Record Generation For Current Day Reservation: Timeout at " + currDate);
        
        try {
            DispatchRecordEntity newDispatch = new DispatchRecordEntity();
            newDispatch.setPickUpOutlet(reservation.getCar().getCurrOutlet());
            newDispatch.setReturnOutlet(reservation.getPickUpOutlet());
            
            Date dispatchTime = reservation.getStartDateTime();
            dispatchTime.setHours(reservation.getStartDateTime().getHours() - 2);
            //newDispatch.setDispatchTime(dispatchTime);

            dispatchRecordSessionBeanLocal.createNewDispatchRecord(newDispatch);
            System.out.println("New Transit Dispatch Record Created for " + reservation.getCar().getLicensePlateNumber());
        } catch (DispatchRecordNameExistsException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }
    }

}
