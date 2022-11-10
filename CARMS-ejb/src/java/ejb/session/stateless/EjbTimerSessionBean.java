/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 *
 * @author stonley
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {

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
            if (true) {
                System.out.println("********** Car <LicensePlateNumber> at Outlet <OutletAddress> allocated to <customerName> for pickup on <reservationStartDate>!");
                System.out.println("********** Car <LicensePlateNumber> at Outlet <OutletAddress> requires transit dispatch!");
            }
        }

//
//        for (ProductEntity productEntity : productEntities) {
//            if (productEntity.getQuantityOnHand().compareTo(productEntity.getReorderQuantity()) <= 0) {
//                System.out.println("********** Product " + productEntity.getSkuCode() + " requires reordering: QOH = " + productEntity.getQuantityOnHand() + "; RQ = " + productEntity.getReorderQuantity());
//            }
//        }
    }

    @Schedule(hour = "2", minute = "1", info = "generateDispatchRecordCurrentDayReservation")
    public void generateDispatchRecordCurrentDayReservation() {
        String currDate = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println("********** Dispatch Record Generation For Current Day Reservation: Timeout at " + currDate);

        //!!need to retrieve all records for current day!!
        List<ReservationEntity> reservations = reservationSessionBeanLocal.retrieveAllReservations(); 
        
        for (ReservationEntity reservation : reservations) {
            if (true) {
                System.out.println("********** New Transit Dispatch Record created for Car <LicensePlateNumber>!");
            }
        }
    }

}
