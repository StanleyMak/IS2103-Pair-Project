/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarEntity;
import entity.CarModelEntity;
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
import util.exception.CustomerNotFoundException;

/**
 *
 * @author stonley
 */
@Stateless
public class CarSessionBean implements CarSessionBeanRemote, CarSessionBeanLocal {

    @EJB(name = "OutletSessionBeanLocal")
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB(name = "CarModelSessionBeanLocal")
    private CarModelSessionBeanLocal carModelSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;
    

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    

    @Override
    public Long createNewCar(CarEntity car, String modelName, String outletAddress) {
        CarModelEntity carModel = carModelSessionBeanLocal.retrieveCarModelByCarModelName(modelName);
        car.setModel(carModel);
        
        OutletEntity outlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(outletAddress);
        car.setCurrOutlet(outlet);
        
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
        Query query = em.createQuery("SELECT c FROM CarEntity c ORDER BY c.model.category.categoryName ASC, c.model.modelMake ASC, c.model.modelName ASC, c.licensePlateNumber ASC");
        List<CarEntity> cars = query.getResultList();
        return cars;
    }
    
    @Override
    public List<CarEntity> retrieveAllCarsOfCarModel(String carModelName) {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.model.modelName = ?1")
                .setParameter(1, carModelName);
        List<CarEntity> cars = query.getResultList();
        return cars;
    }
    
    public List<CarEntity> retrieveAllCarsOfOutlet(String outletAddress) {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.currOutlet.address = ?1")
                .setParameter(1, outletAddress);
        List<CarEntity> cars = query.getResultList();
        return cars;
    }
    
    @Override
    public void updateCar(CarEntity car, String modelName, String outletAddress) {
        CarModelEntity carModel = carModelSessionBeanLocal.retrieveCarModelByCarModelName(modelName);
        car.setModel(carModel);
        
        OutletEntity outlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(outletAddress);
        car.setCurrOutlet(outlet);
                
        em.merge(car);
    }
    
    @Override
    public void deleteCar(Long carID) {
        CarEntity car = retrieveCarByCarID(carID);

        em.remove(car);
    }
    
    public void pickUpCar(String username, String reservationCode) {
        ReservationEntity targetReservation = new ReservationEntity(); 
        // get Customer from his email
        
        CustomerEntity customer = new CustomerEntity(); 
        
        try {
            customer = customerSessionBean.retrieveCustomerByCustomerUsername(username);
        } catch (CustomerNotFoundException ex) {
            System.out.println("Customer does not exist");
        }
        
        
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
        //pickUpOutlet.getCars().add(pickUpCar);
    }
    
    public void returnCar(String username, String reservationCode) {
        ReservationEntity targetReservation = new ReservationEntity(); 
        
        // get Customer from his username
        CustomerEntity customer = new CustomerEntity(); 
        try {
            customer = customerSessionBean.retrieveCustomerByCustomerUsername(username);
        } catch (CustomerNotFoundException ex) {
            System.out.println("Customer not found");
        }
        
        
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
        //pickUpOutlet.getCars().add(pickUpCar);
    }
    
    
}
