/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.OutletEntity;
import entity.ReservationEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CustomerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author hanyang
 */
@Remote
public interface ReservationSessionBeanRemote {
    
    public Long createNewReservation(ReservationEntity reservation, Long carID, String username, String returnOutletAddress, String pickupOutletAddress) throws CustomerNotFoundException;
    
    public ReservationEntity retrieveReservationByID(Long reservationID) throws ReservationNotFoundException;
    
    public ReservationEntity retrieveReservationByReservationCode(String reservationCode) throws ReservationNotFoundException; 
    
    public void deleteReservation(String email, String reservationCode) throws CustomerNotFoundException, ReservationNotFoundException;
    
    public List<ReservationEntity> retrieveAllReservations();
    
    public List<ReservationEntity> retrieveReservationsOfCarID(Long carID);
    
    public List<ReservationEntity> retrieveReservationsOfRentalRateID(Long rentalRateID);
    
    public List<ReservationEntity> retrieveReservationsByCategory(CarCategoryEntity carCategory);
}
