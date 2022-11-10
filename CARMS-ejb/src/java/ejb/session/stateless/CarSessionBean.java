/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarEntity;
import entity.CarModelEntity;
import entity.OutletEntity;
import entity.ReservationEntity;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.StatusEnum;
import util.exception.CarModelNotFoundException;
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
    public Long createNewCar(CarEntity car, String modelName, String outletAddress) throws CarModelNotFoundException {
        try {
            CarModelEntity carModel = carModelSessionBeanLocal.retrieveCarModelByCarModelName(modelName);
            car.setModel(carModel);

            OutletEntity outlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(outletAddress);
            car.setCurrOutlet(outlet);

            em.persist(car);
            em.flush();

            return car.getCarID();
        } catch (CarModelNotFoundException e) {
            throw new CarModelNotFoundException("Car Model Name " + modelName + " does not exist!\n");
        }
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
    public void updateCar(CarEntity car, String modelName, String outletAddress) throws CarModelNotFoundException {
        try {
            CarModelEntity carModel = carModelSessionBeanLocal.retrieveCarModelByCarModelName(modelName);
            car.setModel(carModel);

            OutletEntity outlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(outletAddress);
            car.setCurrOutlet(outlet);

            em.merge(car);
        } catch (CarModelNotFoundException e) {
            throw new CarModelNotFoundException("Car Model Name " + modelName + " does not exist!\n");
        }
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
    public List<CarEntity> doSearchCar(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet) {
        List<CarEntity> cars = retrieveAllCars();
        List<CarEntity> availableCars = new ArrayList<>();

        // available cars = cars that do not have any reservation recrods at specified date
        for (CarEntity car : cars) {
            List<ReservationEntity> carReservations = reservationSessionBean.retrieveReservationsOfCarID(car.getCarID());
            if (car.getCurrOutlet().getAddress().equals(pickupOutlet.getAddress())) {
                boolean potential = true; 
                for (ReservationEntity res : carReservations) {
                    if (res.getStartDateTime().compareTo(returnDateTime) <= 0 ||
                            res.getEndDateTime().compareTo(pickupDateTime) >= 0) {
                        potential = false; 
                        break; 
                    }
                }
                
                if (potential) {
                    availableCars.add(car);
                }
            } else {
                boolean potential = true; 
                for (ReservationEntity res : carReservations) {
                    LocalDateTime startDateTime = LocalDateTime.ofInstant(res.getStartDateTime().toInstant(), ZoneId.systemDefault());
                    LocalDateTime endDateTime = LocalDateTime.ofInstant(res.getEndDateTime().toInstant(), ZoneId.systemDefault());
                    // factor in transit duration (2 hours)
                    endDateTime = endDateTime.plusHours(2);
                    startDateTime = startDateTime.minusHours(2);

                    LocalDateTime pickupDateTimeLocal = LocalDateTime.ofInstant(pickupDateTime.toInstant(), ZoneId.systemDefault());
                    LocalDateTime returnDateTimeLocal = LocalDateTime.ofInstant(returnDateTime.toInstant(), ZoneId.systemDefault());

                    if (startDateTime.compareTo(returnDateTimeLocal) <= 0) {
                        carReservations.remove(res);
                    }
                    
                    if (startDateTime.compareTo(returnDateTimeLocal) <= 0 ||
                            endDateTime.compareTo(pickupDateTimeLocal) >= 0) {
                        potential = false; 
                        break; 
                    }
                    
                    if (potential) {
                        availableCars.add(car);
                    }
                }
            }
        }
        return availableCars;
    }

}

