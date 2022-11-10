/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CustomerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author hanyang
 */
@Local
public interface ReservationSessionBeanLocal {
    
    public Long createNewReservation(ReservationEntity reservation, Long carID, String username, String returnOutletAddress, String pickupOutletAddress) throws CustomerNotFoundException;
    
    public ReservationEntity retrieveReservationByID(Long reservationID) throws ReservationNotFoundException;
    
    public ReservationEntity retrieveReservationByReservationCode(String reservationCode) throws ReservationNotFoundException; 
    
    public void deleteReservation(String email, String reservationCode) throws CustomerNotFoundException, ReservationNotFoundException;
    
    // public List<ReservationEntity> retrieveAvailableCars(Date pickupDateTime, Date returnDateTime, String pickUpOutlet, String returnOutlet); 
    
    public List<ReservationEntity> retrieveAllReservations();

    public List<ReservationEntity> retrieveReservationsOfCarID(Long carID);
    
}
