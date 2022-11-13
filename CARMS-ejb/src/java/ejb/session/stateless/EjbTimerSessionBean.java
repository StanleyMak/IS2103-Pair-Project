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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import util.enumeration.StatusEnum;
import util.exception.CarModelNotFoundException;
import util.exception.CarNotFoundException;
import util.exception.DispatchRecordNameExistsException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

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

        List<ReservationEntity> reservations = reservationSessionBeanLocal.retrieveReservationsForCurrentDay(new Date());

        try {
            for (ReservationEntity reservation : reservations) {
                CarCategoryEntity carCategory = reservation.getCarCategory();
                OutletEntity pickupOutlet = reservation.getPickUpOutlet();
                OutletEntity returnOutlet = reservation.getReturnOutlet();

                // Assigning cars of the same outlet
                CarEntity carOfCategoryOfOutlet = carSessionBeanLocal.retrievePotentialCarOfCategoryOfOutlet(reservation.getStartDateTime(), reservation.getEndDateTime(), pickupOutlet, returnOutlet, carCategory);

                if (carOfCategoryOfOutlet != null) {
                    carSessionBeanLocal.allocateCarToReservation(carOfCategoryOfOutlet.getCarID(), reservation.getReservationID());
                    System.out.println("********** Car " + carOfCategoryOfOutlet.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + reservation.getStartDateTime() + "!\n");
                    break;
                }

                //Assigning cars of other outlet
                CarEntity carOfCategoryOfOtherOutlet = carSessionBeanLocal.retrievePotentialCarOfCategoryOfOtherOutlet(reservation.getStartDateTime(), reservation.getEndDateTime(), pickupOutlet, returnOutlet, carCategory);

                carSessionBeanLocal.allocateCarToReservation(carOfCategoryOfOtherOutlet.getCarID(), reservation.getReservationID());
                System.out.println("********** Car " + carOfCategoryOfOtherOutlet.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + dateTimeFormat.format(reservation.getStartDateTime()) + "!\n");
                System.out.println("********** Car <LicensePlateNumber> at Outlet <OutletAddress> requires transit dispatch!");

                generateDispatchRecordCurrentDayReservation(reservation);
            }
        } catch (ReservationNotFoundException | CarNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

    }

    public void allocateCarsCurrentDayReservationManually() {
        String currDate = dateTimeFormat.format(new Date());
        System.out.println("********** Car Allocation For Current Day Reservation: Timeout at " + currDate);

        List<ReservationEntity> reservations = reservationSessionBeanLocal.retrieveReservationsForCurrentDay(new Date());

        try {
            for (ReservationEntity reservation : reservations) {
                CarCategoryEntity carCategory = reservation.getCarCategory();
                OutletEntity pickupOutlet = reservation.getPickUpOutlet();
                OutletEntity returnOutlet = reservation.getReturnOutlet();

                // Assigning cars of the same outlet
                CarEntity carOfCategoryOfOutlet = carSessionBeanLocal.retrievePotentialCarOfCategoryOfOutlet(reservation.getStartDateTime(), reservation.getEndDateTime(), pickupOutlet, returnOutlet, carCategory);

                if (carOfCategoryOfOutlet != null) {
                    try {
                        carSessionBeanLocal.allocateCarToReservation(carOfCategoryOfOutlet.getCarID(), reservation.getReservationID());
                    } catch (CarNotFoundException ex) {
                        Logger.getLogger(EjbTimerSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("********** Car " + carOfCategoryOfOutlet.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + reservation.getStartDateTime() + "!\n");
                    break;
                }

                CarEntity carOfCategoryOfOtherOutlet = carSessionBeanLocal.retrievePotentialCarOfCategoryOfOtherOutlet(reservation.getStartDateTime(), reservation.getEndDateTime(), pickupOutlet, returnOutlet, carCategory);
                carSessionBeanLocal.allocateCarToReservation(carOfCategoryOfOtherOutlet.getCarID(), reservation.getReservationID());
                System.out.println("********** Car " + carOfCategoryOfOtherOutlet.getLicensePlateNumber() + " at Outlet " + pickupOutlet.getAddress() + " allocated to " + customerSessionBeanLocal.retrieveCustomerOfReservationID(reservation.getReservationID()) + " for pickup on " + dateTimeFormat.format(reservation.getStartDateTime()) + "!\n");
                System.out.println("********** Car <LicensePlateNumber> at Outlet <OutletAddress> requires transit dispatch!");

                generateDispatchRecordCurrentDayReservation(reservation);
            }
        } catch (ReservationNotFoundException | CarNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

    }

    private void generateDispatchRecordCurrentDayReservation(ReservationEntity reservation) {
        try {
            String currDate = dateTimeFormat.format(new Date());
            System.out.println("********** Dispatch Record Generation For Current Day Reservation: Timeout at " + currDate);

            DispatchRecordEntity newDispatch = new DispatchRecordEntity();
            CarEntity car = reservation.getCar();
            car.setStatus(StatusEnum.IN_TRANSIT);
            carSessionBeanLocal.updateCarEntity(car);

            newDispatch.setPickUpOutlet(reservation.getCar().getCurrOutlet());
            newDispatch.setReturnOutlet(reservation.getPickUpOutlet());

            LocalDateTime dispatchDateTimeLocal = LocalDateTime.ofInstant(reservation.getStartDateTime().toInstant(), ZoneId.systemDefault());
            dispatchDateTimeLocal = dispatchDateTimeLocal.minusHours(2);
            Date dispatchDateTime = Date.from(dispatchDateTimeLocal.atZone(ZoneId.systemDefault()).toInstant());

            newDispatch.setPickUpTime(dispatchDateTime);
            newDispatch.setIsCompleted(Boolean.FALSE);
            newDispatch.setReservation(reservation);
            newDispatch.setDispatchRecordName(reservation.getReservationCode() + "dispatch");

            dispatchRecordSessionBeanLocal.createNewDispatchRecord(newDispatch);
            System.out.println("New Transit Dispatch Record Created for " + reservation.getCar().getLicensePlateNumber() + "at " + reservation.getCar().getCurrOutlet() + " at " + dispatchDateTime.toString());
        } catch (DispatchRecordNameExistsException | UnknownPersistenceException | InputDataValidationException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }
    }
}
