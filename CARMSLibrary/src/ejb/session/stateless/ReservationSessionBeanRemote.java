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
import util.exception.CarCategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.PartnerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ReservationCodeExistsException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author hanyang
 */
@Remote
public interface ReservationSessionBeanRemote {

    public String createNewReservation(ReservationEntity reservation, String email, String returnOutletAddress, String pickupOutletAddress, String carCategoryName) throws CustomerNotFoundException, CarCategoryNotFoundException, UnknownPersistenceException, InputDataValidationException, ReservationCodeExistsException;

    public ReservationEntity retrieveReservationByID(Long reservationID) throws ReservationNotFoundException;

    public ReservationEntity retrieveReservationByReservationCode(String reservationCode) throws ReservationNotFoundException;

    public void deleteReservation(String email, String reservationCode) throws CustomerNotFoundException, ReservationNotFoundException;

    public List<ReservationEntity> retrieveAllReservations();

    public List<ReservationEntity> retrieveReservationsOfCarID(Long carID);

    public List<ReservationEntity> retrieveReservationsOfRentalRateID(Long rentalRateID);

    public List<ReservationEntity> retrieveReservationsByCategory(String carCategoryName);

    public String cancelReservation(String email, String reservationCode, Date currDate) throws ReservationNotFoundException, CustomerNotFoundException;

    public List<ReservationEntity> retrieveReservationsOfPartnerID(Long partnerID);

    public String cancelReservationForPartner(String username, String reservationCode, Date currDate) throws ReservationNotFoundException, PartnerNotFoundException;

    public void deleteReservationForPartner(String username, String reservationCode) throws PartnerNotFoundException, ReservationNotFoundException;

    public String createNewReservationForPartner(ReservationEntity reservation, String username, String returnOutletAddress, String pickupOutletAddress, String carCategoryName) throws PartnerNotFoundException, CarCategoryNotFoundException;

    public void updateReservation(ReservationEntity res);

}
