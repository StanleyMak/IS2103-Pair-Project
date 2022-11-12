/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.ReservationEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CarCategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author hanyang
 */
@Local
public interface ReservationSessionBeanLocal {
    
    public Long createNewReservation(ReservationEntity reservation, String email, String returnOutletAddress, String pickupOutletAddress, String carCategoryName) throws CustomerNotFoundException, CarCategoryNotFoundException;
    
    public ReservationEntity retrieveReservationByID(Long reservationID) throws ReservationNotFoundException;
    
    public ReservationEntity retrieveReservationByReservationCode(String reservationCode) throws ReservationNotFoundException; 
    
    public void deleteReservation(String email, String reservationCode) throws CustomerNotFoundException, ReservationNotFoundException;
    
    public List<ReservationEntity> retrieveAllReservations();

    public List<ReservationEntity> retrieveReservationsOfCarID(Long carID);

    public List<ReservationEntity> retrieveReservationsOfRentalRateID(Long rentalRateID);

    public List<ReservationEntity> retrieveReservationsForCurrentDay(Date currDay);

    public List<ReservationEntity> retrieveReservationsByCategory(String carCategoryName);

    
}
