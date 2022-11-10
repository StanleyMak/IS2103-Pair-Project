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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.StatusEnum;
import util.exception.CustomerNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author stonley
 */
@Stateless
public class CarSessionBean implements CarSessionBeanRemote, CarSessionBeanLocal {

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB(name = "OutletSessionBeanLocal")
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB(name = "CarModelSessionBeanLocal")
    private CarModelSessionBeanLocal carModelSessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
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

    @Override
    public void pickUpCar(ReservationEntity reservation) {
        CarEntity pickUpCar = reservation.getCar();
        em.persist(pickUpCar);
        // update car status to rented out
        pickUpCar.setStatus(StatusEnum.ON_RENTAL);
        // update car location to pick up location
        pickUpCar.setCurrOutlet(reservation.getPickUpOutlet());
        em.flush();
    }

    @Override
    public void returnCar(String reservationCode) {
        ReservationEntity reservation = new ReservationEntity();
        try {
            reservation = reservationSessionBean.retrieveReservationByReservationCode(reservationCode);
        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation does not exist " + ex.getMessage());
        }
        CarEntity returnCar = reservation.getCar();
        // update car status to available
        returnCar.setStatus(StatusEnum.AVAILABLE);
        returnCar.setCurrOutlet(reservation.getReturnOutlet());
        OutletEntity pickUpOutlet = reservation.getPickUpOutlet();
    }

    @Override
    public void doSearchCar() {
        List<ReservationEntity> reservations = reservationSessionBean.retrieveAllReservations();
        List<ReservationEntity> availableResverations = new ArrayList<>();
        /*
         for (ReservationEntity res : reservations) {
             // IF NOT SERVICING, NOT DISABLED
             // IF REQUESTED OUTLET IS = RETURN OUTLET
             if (returnDateTime.compareTo(res.getStartDateTime()) < 0) {
                 if (res.getEndDateTime().compareTo(pickupDateTime) < 0) {
                     
                 }
             }
         */
        // ELSE (REQUEST OUTLET != RETURN OUTLET)
        // CHECK reservation + transit time (2 hors) ends before pickupdate
        // CHECK reservation starts after return date + 2hours
    }
}

