/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarEntity;
import entity.CustomerEntity;
import entity.OutletEntity;
import entity.ReservationEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.StatusEnum;

/**
 *
 * @author stonley
 */
@Stateless
public class CarSessionBean implements CarSessionBeanRemote, CarSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;
    

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewCar(CarEntity car) {
        em.persist(car);
        em.flush();
        
        return car.getCarID();
    }
    
    @Override
    public CarEntity retrieveCarByCarID(Long carID) {
        CarEntity car = em.find(CarEntity.class, carID);
        //car.getXX().size();
        return car;
    }

    @Override
    public CarEntity retrieveCarByCarLicensePlateNumber(String licensePlateNumber) {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.licensePlateNumber = ?1")
                .setParameter(1, licensePlateNumber);
        CarEntity car = (CarEntity) query.getSingleResult();
        //car.getXX().size();
        return car;
    }
    
    @Override
    public List<CarEntity> retrieveAllCars() {
        Query query = em.createQuery("SELECT c FROM CarEntity c");
        List<CarEntity> cars = query.getResultList();
        return cars;
    }
    
    @Override
    public void updateCar(CarEntity car) {
        em.merge(car);
    }
    
    @Override
    public void deleteCar(Long carID) {
        CarEntity car = retrieveCarByCarID(carID);
        
        //dissociate
        em.remove(car);
    }
    
    public void pickUpCar(String email, String reservationCode) {
        ReservationEntity targetReservation = new ReservationEntity(); 
        // get Customer from his email
        CustomerEntity customer = customerSessionBean.retrieveCustomerByCustomerUsername(email);
        
        // association?
        // get the reservation
        List<ReservationEntity> reservations = customer.getReservations();
        for (ReservationEntity res : reservations) {
            if (reservationCode.equals(res.getReservationCode())) {
                targetReservation = res;
            }
        }
        
        
        // update car status to rented out
        CarEntity pickUpCar = targetReservation.getCar();
        pickUpCar.setStatus(StatusEnum.ON_RENTAL);
        // update car location to pick up location, bidirectional
        pickUpCar.setCurrOutlet(targetReservation.getPickUpOutlet());
        OutletEntity pickUpOutlet = targetReservation.getPickUpOutlet();
        pickUpOutlet.getCars().add(pickUpCar);
    }
    
    public void returnCar(String email, String reservationCode) {
        ReservationEntity targetReservation = new ReservationEntity(); 
        // get Customer from his email
        CustomerEntity customer = customerSessionBean.retrieveCustomerByCustomerUsername(email);
        
        // association?
        // get the reservation
        List<ReservationEntity> reservations = customer.getReservations();
        for (ReservationEntity res : reservations) {
            if (reservationCode.equals(res.getReservationCode())) {
                targetReservation = res;
            }
        }
        
        // update car status to rented out
        CarEntity pickUpCar = targetReservation.getCar();
        pickUpCar.setStatus(StatusEnum.ON_RENTAL);
        // update car location to pick up location, bidirectional
        pickUpCar.setCurrOutlet(targetReservation.getPickUpOutlet());
        OutletEntity pickUpOutlet = targetReservation.getPickUpOutlet();
        pickUpOutlet.getCars().add(pickUpCar);
    }
    
    
}
