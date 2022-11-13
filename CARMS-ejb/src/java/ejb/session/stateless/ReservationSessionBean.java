/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.CustomerEntity;
import entity.OutletEntity;
import entity.OwnCustomerEntity;
import entity.PartnerEntity;
import entity.RentalRateEntity;
import entity.ReservationEntity;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CarCategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author hanyang
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @EJB(name = "CarCategorySessionBeanLocal")
    private CarCategorySessionBeanLocal carCategorySessionBeanLocal;

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBeanLocal;

    @EJB(name = "OutletSessionBeanLocal")
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @Override
    public String createNewReservation(ReservationEntity reservation, String email, String returnOutletAddress, String pickupOutletAddress, String carCategoryName) throws CustomerNotFoundException, CarCategoryNotFoundException {
        //AUTO ASSIGN THE CHEAPEST RENTAL RATE PER DAY!!!!!!!!!!!!!!
        // missing partner

        try {
            OwnCustomerEntity customer = customerSessionBeanLocal.retrieveOwnCustomerByOwnCustomerEmail(email);
            OutletEntity pickupOutlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(pickupOutletAddress);
            OutletEntity returnOutlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(returnOutletAddress);
            CarCategoryEntity carCategory = null;
            try {
                carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(carCategoryName);
            } catch (CarCategoryNotFoundException e) {
                throw new CarCategoryNotFoundException("Car Category Not Found");
            }
            reservation.setCarCategory(carCategory);
            reservation.setReturnOutlet(returnOutlet);
            reservation.setPickUpOutlet(pickupOutlet);
            customer.getReservations().add(reservation);

            em.persist(reservation);
            em.flush();

            return reservation.getReservationCode();
        } catch (CustomerNotFoundException e) {
            throw new CustomerNotFoundException("Customer Not Found");
        }

    }
    
    @Override
    public String createNewReservationForPartner(ReservationEntity reservation, String username, String returnOutletAddress, String pickupOutletAddress, String carCategoryName) throws PartnerNotFoundException, CarCategoryNotFoundException {

        try {
            PartnerEntity partner = partnerSessionBeanLocal.retrievePartnerByUsername(username);
            OutletEntity pickupOutlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(pickupOutletAddress);
            OutletEntity returnOutlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(returnOutletAddress);
            CarCategoryEntity carCategory = null;
            try {
                carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(carCategoryName);
            } catch (CarCategoryNotFoundException e) {
                throw new CarCategoryNotFoundException("Car Category Not Found");
            }
            reservation.setCarCategory(carCategory);
            reservation.setReturnOutlet(returnOutlet);
            reservation.setPickUpOutlet(pickupOutlet);
            reservation.setPartner(partner);

            em.persist(reservation);
            em.flush();

            return reservation.getReservationCode();
        } catch (PartnerNotFoundException e) {
            throw new PartnerNotFoundException("Partner Not Found");
        }

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
        Query query = em.createQuery("SELECT rc FROM ReservationEntity rc WHERE rc.reservationCode = ?1")
                .setParameter(1, reservationCode);

        try {
            ReservationEntity reservation = (ReservationEntity) query.getSingleResult();
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
    public String cancelReservation(String email, String reservationCode, Date currDate) throws ReservationNotFoundException, CustomerNotFoundException {
        LocalDateTime currDateLocal = LocalDateTime.ofInstant(currDate.toInstant(), ZoneId.systemDefault());
        ReservationEntity res = null;
        try {
            res = retrieveReservationByReservationCode(reservationCode);
        } catch (ReservationNotFoundException e) {
            throw new ReservationNotFoundException("Reservation Not Found");
        }
        Date pickUpDate = res.getStartDateTime();
        LocalDateTime pickUpDateLocal = LocalDateTime.ofInstant(pickUpDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime penalty14 = pickUpDateLocal.minusDays(14);
        LocalDateTime penalty7 = pickUpDateLocal.minusDays(7);
        LocalDateTime penalty3 = pickUpDateLocal.minusDays(3);
        double rentalFee = res.getRentalFee();
        double penaltyFee = 0;
        
        if (currDateLocal.isAfter(penalty3)) {
            penaltyFee = 0.7 * rentalFee;
        } else if (currDateLocal.isAfter(penalty7)) {
            penaltyFee = 0.5 * rentalFee;
        } else if (currDateLocal.isAfter(penalty14)) {
            penaltyFee = 0.2 * rentalFee;
        }
        
        try {
            this.deleteReservation(email, reservationCode);
            System.out.println("Reservation " + reservationCode + " successfully cancelled!\n");
        } catch (CustomerNotFoundException customerNotFoundException) {
            throw new CustomerNotFoundException("Customer Not Found");
        } catch (ReservationNotFoundException reservationNotFoundException) {
            throw new ReservationNotFoundException("Reservation Not Found");
        }
        
        if (res.isOnlinePayment()) {
            return "Amount of $" + (rentalFee - penaltyFee) + " has been refunded after charging a cancellation fee of $" + penaltyFee;
        } else {
            return "Cancellation fee of $" + penaltyFee + " has been charged";
        }

    }
    
    @Override
    public String cancelReservationForPartner(String username, String reservationCode, Date currDate) throws ReservationNotFoundException, PartnerNotFoundException {
        LocalDateTime currDateLocal = LocalDateTime.ofInstant(currDate.toInstant(), ZoneId.systemDefault());
        ReservationEntity res = null;
        try {
            res = retrieveReservationByReservationCode(reservationCode);
        } catch (ReservationNotFoundException e) {
            throw new ReservationNotFoundException("Reservation Not Found");
        }
        Date pickUpDate = res.getStartDateTime();
        LocalDateTime pickUpDateLocal = LocalDateTime.ofInstant(pickUpDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime penalty14 = pickUpDateLocal.minusDays(14);
        LocalDateTime penalty7 = pickUpDateLocal.minusDays(7);
        LocalDateTime penalty3 = pickUpDateLocal.minusDays(3);
        double rentalFee = res.getRentalFee();
        double penaltyFee = 0;
        
        if (currDateLocal.isAfter(penalty3)) {
            penaltyFee = 0.7 * rentalFee;
        } else if (currDateLocal.isAfter(penalty7)) {
            penaltyFee = 0.5 * rentalFee;
        } else if (currDateLocal.isAfter(penalty14)) {
            penaltyFee = 0.2 * rentalFee;
        }
        
        try {
            this.deleteReservationForPartner(username, reservationCode);
            System.out.println("Reservation " + reservationCode + " successfully cancelled!\n");
        } catch (PartnerNotFoundException customerNotFoundException) {
            throw new PartnerNotFoundException("Partner Not Found");
        } catch (ReservationNotFoundException reservationNotFoundException) {
            throw new ReservationNotFoundException("Reservation Not Found");
        }
        
        if (res.isOnlinePayment()) {
            return "Amount of $" + (rentalFee - penaltyFee) + " has been refunded after charging a cancellation fee of $" + penaltyFee;
        } else {
            return "Cancellation fee of $" + penaltyFee + " has been charged";
        }

    }
    
    @Override
    public void deleteReservationForPartner(String username, String reservationCode) throws PartnerNotFoundException, ReservationNotFoundException {
        try {
            PartnerEntity partner = partnerSessionBeanLocal.retrievePartnerByUsername(username);
            ReservationEntity reservationToDelete = retrieveReservationByReservationCode(reservationCode);
            List<ReservationEntity> res = retrieveReservationsOfPartnerID(partner.getPartnerID());
            res.remove(reservationToDelete);
            
            em.remove(reservationToDelete);
        } catch (PartnerNotFoundException e) {
            throw new PartnerNotFoundException("Partner Not Found");
        } catch (ReservationNotFoundException e) {
            throw new ReservationNotFoundException("Reservation Not Found");
        }
    }

    @Override
    public void deleteReservation(String email, String reservationCode) throws CustomerNotFoundException, ReservationNotFoundException {
        CustomerEntity customer = customerSessionBeanLocal.retrieveOwnCustomerByOwnCustomerEmail(email);
        ReservationEntity reservationToDelete = retrieveReservationByReservationCode(reservationCode);
        customer.getReservations().remove(reservationToDelete);

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

    private Boolean containsRentalRateID(ReservationEntity res, Long rentalRateID) {
        List<RentalRateEntity> rentalRates = res.getRentalRates();
        for (RentalRateEntity rentalRate : rentalRates) {
            if (rentalRate.getRentalRateID() == rentalRateID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ReservationEntity> retrieveReservationsOfRentalRateID(Long rentalRateID) {
        List<ReservationEntity> correctReservations = new ArrayList<>();
        List<ReservationEntity> reservations = retrieveAllReservations();
        for (ReservationEntity res : reservations) {
            if (containsRentalRateID(res, rentalRateID)) {
                correctReservations.add(res);
            }
        }
        return correctReservations;

    }

    //review date = ?1
    @Override
    public List<ReservationEntity> retrieveReservationsForCurrentDay(Date currDay) {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.startDateTime = ?1")
                .setParameter(1, currDay);
        List<ReservationEntity> reservations = query.getResultList();
        return reservations;
    }
    
    @Override
    public List<ReservationEntity> retrieveReservationsOfPartnerID(Long partnerID) {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.partner.partnerID = ?1")
                .setParameter(1, partnerID);
        List<ReservationEntity> reservations = query.getResultList();
        return reservations;
    }
    
    @Override
    public void updateReservation(ReservationEntity res) {
        em.merge(res);
    }

    public List<ReservationEntity> retrieveReservationsByCategory(String carCategoryName) {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.carCategory.categoryName = ?1")
                .setParameter(1, carCategoryName);
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
