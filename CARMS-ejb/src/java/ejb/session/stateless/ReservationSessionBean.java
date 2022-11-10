/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarEntity;
import entity.CustomerEntity;
import entity.OutletEntity;
import entity.OwnCustomerEntity;
import entity.ReservationEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author hanyang
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBean;

    @EJB(name = "OutletSessionBeanLocal")
    private OutletSessionBeanLocal outletSessionBean;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBean;
    
    
    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createNewReservation(ReservationEntity reservation, Long carID, String email, String returnOutletAddress, String pickupOutletAddress) throws CustomerNotFoundException {
        
        // missing partner
        
        CarEntity car = carSessionBean.retrieveCarByCarID(carID);
        OwnCustomerEntity customer = customerSessionBean.retrieveOwnCustomerByOwnCustomerEmail(email);
        OutletEntity pickupOutlet = outletSessionBean.retrieveOutletByOutletAddress(pickupOutletAddress);
        OutletEntity returnOutlet = outletSessionBean.retrieveOutletByOutletAddress(returnOutletAddress);
        
        reservation.setCar(car);
        reservation.setReturnOutlet(returnOutlet);
        reservation.setPickUpOutlet(pickupOutlet);
        customer.getReservations().add(reservation);
        
        em.persist(reservation);
        em.flush(); 
        
        return reservation.getReservationID();
    }
    
    
    @Override
    public ReservationEntity retrieveReservationByID(Long reservationID) throws ReservationNotFoundException {
        ReservationEntity reservation = em.find(ReservationEntity.class, reservationID);
        //reservation.getXX().size();
        if (reservation != null) {
            return reservation;
        } else {
            throw new ReservationNotFoundException("Reservation " + reservationID + "does not exist!");
        }
    }
    
    @Override
    public ReservationEntity retrieveReservationByReservationCode(String reservationCode) throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT rc FROM ReservationEntity rc WHERE rc.resverationCode LIKE ?1")
                .setParameter(1, reservationCode); 
        
        try {
            ReservationEntity reservation  = (ReservationEntity) query.getSingleResult(); 
            return reservation;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ReservationNotFoundException("Reservation " + reservationCode + "does not exist");
        }
        
        //reservation.getXX().size();
    }
    
    /*
    @Override
    public void deleteReservation(Long reservationID) throws DeleteReservationException, ReservationNotFoundException {
        ReservationEntity reservationToDelete = retrieveReservationByID(reservationID);
        //dissociate
        
        if(reservationToDelete.getCar() == null && reservationToDelete.getPickUpOutlet() == null && reservationToDelete.getReturnOutlet() == null) {
            em.remove(reservationToDelete);
        } else {
            throw new DeleteReservationException("Reservation " + reservationID + " is associated with existing car and outlets");
        }
    }
*/
    
    @Override
    public void deleteReservation(String email, String reservationCode) throws CustomerNotFoundException, ReservationNotFoundException {
        CustomerEntity customer = customerSessionBean.retrieveOwnCustomerByOwnCustomerEmail(email);
        ReservationEntity reservationToDelete = retrieveReservationByReservationCode(reservationCode);
        customer.setReservations(null);
        
        em.remove(reservationToDelete);
    }
    
    
    @Override 
    public List<ReservationEntity> retrieveAllReservations() {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r");
        List<ReservationEntity> reservations = query.getResultList();
        return reservations;
    }
    
    @Override
    public List<ReservationEntity> retrieveReservationsOfCarID(Long carID) {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.car.carID = ?1")
                .setParameter(1, carID); 
        List<ReservationEntity> reservations = query.getResultList(); 
        return reservations; 
    }
    
    /*
    @Override
    public List<ReservationEntity> retrieveAvailableCars(Date pickupDateTime, Date returnDateTime, String pickupOutletAddress, String returnOutletAddress) {
          
        // query for available cars based on pickup time and return time
        Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE pickUpDateTime >= r.endDateTime AND returnDateTime <= r.startDateTime");
        List<ReservationEntity> availResGivenDateTime = query.getResultList(); 
        
        OutletEntity pickupOutlet = outletSessionBean.retrieveOutletByOutletAddress(pickupOutletAddress);
        Date pickupOutletOpeningHours = pickupOutlet.getOpenHour();
        OutletEntity returnOutlet = outletSessionBean.retrieveOutletByOutletAddress(returnOutletAddress);
        Date returnOutletClosingHours = pickupOutlet.getCloseHour(); 
        
        System.out.println("List of available cars:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        // check outlet
        for (ReservationEntity res : availResGivenDateTime) {
            CarEntity car = res.getCar();
            if (car.getStatus().equals(StatusEnum.AVAILABLE)) {
                if (car.getCurrOutlet().getAddress().equals(pickupOutletAddress)) {
                    if (pickupOutletOpeningHours <= pickupDateTime && returnDateTime <= returnOutletClosingHours) {
                        
                        // need to add rental fee of car rentalFee = rentalRatePerDay * numDays
                        
                        
                        
                        System.out.printf("%-5s%-5s%-40s%-15s%-15s\n", "car.getModel().getCategory()", "car.getModel().getModelMake()", "car.getModel().getModelName()");   
                    }       
                } else { // different outlet
                    // add 2 hours
                    if (pickupOutletOpeningHours <= pickupDateTime && returnDateTime <= returnOutletClosingHours) {
                        System.out.printf("%-5s%-5s%-40s%-15s%-15s\n", "car.getModel().getCategory()", "car.getModel().getModelMake()", "car.getModel().getModelName()");
                    }
                }
            }
        }
        
        // check for sufficient inventory
    }
*/
    
    
}
