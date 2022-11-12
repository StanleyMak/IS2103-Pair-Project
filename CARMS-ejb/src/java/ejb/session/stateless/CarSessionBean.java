/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
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
import util.exception.CarModelDisabledException;
import util.exception.CarModelNotFoundException;
import util.exception.DeleteCarException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author stonley
 */
@Stateless
public class CarSessionBean implements CarSessionBeanRemote, CarSessionBeanLocal {

    @EJB(name = "RentalRateSessionBeanLocal")
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

    @EJB(name = "CarCategorySessionBeanLocal")
    private CarCategorySessionBeanLocal carCategorySessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "OutletSessionBeanLocal")
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB(name = "CarModelSessionBeanLocal")
    private CarModelSessionBeanLocal carModelSessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewCar(CarEntity car, String modelName, String outletAddress) throws CarModelNotFoundException, CarModelDisabledException {
        try {
            CarModelEntity carModel = carModelSessionBeanLocal.retrieveCarModelByCarModelName(modelName);
            if (!carModel.getIsDisabled()) {
                car.setModel(carModel);

                OutletEntity outlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(outletAddress);
                car.setCurrOutlet(outlet);

                em.persist(car);
                em.flush();
            } else {
                throw new CarModelDisabledException("Car Model " + modelName + " is disabled");
            }

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

    @Override
    public List<CarEntity> retrieveAllCarsOfOutlet(String outletAddress) {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.currOutlet.address = ?1")
                .setParameter(1, outletAddress);
        List<CarEntity> cars = query.getResultList();
        return cars;
    }

    public List<CarEntity> retrieveAllCarsOfCarCategory(String carCategoryName) {
        List<CarEntity> cars = new ArrayList<>();
        List<CarModelEntity> carModels = carModelSessionBeanLocal.retrieveCarModelsOfCarCategory(carCategoryName);
        for (CarModelEntity carModel : carModels) {
            cars.addAll(retrieveAllCarsOfCarModel(carModel.getModelName()));
        }
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
    public void deleteCar(Long carID) throws DeleteCarException {
        CarEntity car = retrieveCarByCarID(carID);

        if (reservationSessionBeanLocal.retrieveReservationsOfCarID(carID).isEmpty()) {
            em.remove(car);
        } else {
            car.setStatus(StatusEnum.DISABLED);
            throw new DeleteCarException("Car ID " + carID + " is associated with existing reservation(s) and cannot be deleted!");
        }
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
            reservation = reservationSessionBeanLocal.retrieveReservationByReservationCode(reservationCode);
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
    public void doSearchCar(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet) {
        
        List<CarCategoryEntity> availableCategories = new ArrayList<>(); 
        
        List<CarEntity> availableCars = getAvailableCars(); 
        
        List<CarCategoryEntity> allCategories = carCategorySessionBean.retrieveAllCarCategory(); 
        
        // retrieve reservations of category and count num reservations per category
        int numAvailCars = 0;
        for (CarCategoryEntity cc : allCategories) {
            
            int numRes = getNumIntersectedReservations(pickupDateTime, returnDateTime);
            
            for (CarEntity car : availableCars) {
                if (car.getModel().getCategory().equals(cc)) {
                    numAvailCars++; 
                }
            }
            if (numRes < numAvailCars) {
                availableCategories.add(cc);
            }
        }
        
        for (CarCategoryEntity carCategory : availableCategories) {
            double rentalRate = rentalRateSessionBeanLocal.computeCheapestRentalRateFee(pickupDateTime, returnDateTime, carCategory.getCategoryName());
            System.out.println(carCategory + " " + rentalRate);
        }
    }
    
    public List<CarEntity> getAvailableCars() {
        List<CarEntity> cars = retrieveAllCars();
        List<CarEntity> availableCars = new ArrayList<>();
        for (CarEntity car : cars) {
            List<ReservationEntity> carReservations = reservationSessionBeanLocal.retrieveReservationsOfCarID(car.getCarID());

            // add cars with no reservations
            if (carReservations.isEmpty() && (!car.getStatus().equals(StatusEnum.DISABLED) && !car.getStatus().equals(StatusEnum.REPAIR))) {
                availableCars.add(car);
                continue;
            }

            if (car.getCurrOutlet().getAddress().equals(pickupOutlet.getAddress())) {
                boolean potential = true;
                for (ReservationEntity res : carReservations) {
                    if (pickupDateTime.compareTo(res.getEndDateTime()) < 0 && returnDateTime.compareTo(res.getStartDateTime()) > 0) {
                        potential = false;
                        break;
                    }
                }

                if (potential) {
                    if (!car.getStatus().equals(StatusEnum.DISABLED) && !car.getStatus().equals(StatusEnum.REPAIR)) {
                        availableCars.add(car);
                    }
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

                    if (pickupDateTimeLocal.compareTo(endDateTime) < 0 && returnDateTimeLocal.compareTo(startDateTime) > 0) {
                        potential = false;
                        break;
                    }
                }

                if (potential) {
                    if (!car.getStatus().equals(StatusEnum.DISABLED) && !car.getStatus().equals(StatusEnum.REPAIR)) {
                        availableCars.add(car);
                    }
                }
            }
        }
        
        return availableCars; 
    } 
    
    @Override
    public List<CarCategoryEntity> searchAvailableCarCategory(Date rNewStart, Date rNewEnd) {

        List<CarCategoryEntity> allCategories = retrieveAllCarCategories();

        List<CarCategoryEntity> availableCategories = new ArrayList<>();

        for (CarCategoryEntity cc : allCategories) {

            // get number of reservations during selected reservation time

            List<ReservationEntity> allReservations = cc.getReservations();

            int numOfReservations = 0;

            for (ReservationEntity r : allReservations) {

                Date rExistingStart = r.getReservationStartDate();

                Date rExistingEnd = r.getReservationEndDate();




                if ((rNewStart.before(rExistingEnd) && rNewStart.after(rExistingStart))

                        || (rNewEnd.after(rExistingStart) && rNewEnd.before(rExistingEnd))

                        || (rNewStart.before(rExistingStart) && rNewEnd.after(rExistingEnd))

                        || (rNewStart.equals(rExistingStart) && rNewEnd.equals(rExistingEnd))) {

                    numOfReservations++;

                }

            }

            // get number of cars available at any one point of time

            List<CarEntity> cars = carSessionBeanLocal.retrieveCarsByCarCategory(cc);

            List<CarEntity> availableCars = new ArrayList<>();

            for (CarEntity c:cars) {

                if (!c.getIsDisabled()) {

                    availableCars.add(c); 

                }

            }

            if (numOfReservations < availableCars.size()) {

                availableCategories.add(cc);

            }

        }

        return availableCategories;
    }

    public int getNumIntersectedReservations(Date newStartDate, Date newEndDate) {
        int numRes = 0; 
        List<ReservationEntity> allReservations = reservationSessionBeanLocal.retrieveReservationsByCategory(cc);           
        for (ReservationEntity res : allReservations) {
            Date existingStartDate = res.getStartDateTime(); 
            Date existingEndDate = res.getEndDateTime(); 
            
            if (newStartDate.before(existingEndDate) && newStartDate.after(existingStartDate) ||
                    newEndDate.after(existingStartDate) && newEndDate.before(existingEndDate) ||
                    newStartDate.before(existingStartDate) && newEndDate.after(existingEndDate) ||
                    newStartDate.equals(existingStartDate) && newEndDate.equals(existingEndDate)) {
                numRes++; 
            }
        }
         return numRes;    
    }
    
    @Override
    public void allocateCarToReservation(Long carID, Long reservationID) throws ReservationNotFoundException {
        CarEntity car = retrieveCarByCarID(carID);
        ReservationEntity reservation = null;
        try {
            reservation = reservationSessionBeanLocal.retrieveReservationByID(reservationID);
        } catch (ReservationNotFoundException e) {
            throw new ReservationNotFoundException("Reservation " + reservationID + " not found");
        }
        reservation.setCar(car);
    }

    @Override
    public List<CarEntity> retrieveCarsFilteredByCarCategory(String carCategoryName) {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.model.category.categoryName = ?1")
                .setParameter(1, carCategoryName);
        List<CarEntity> cars = query.getResultList();
        return cars;
    }

    @Override
    public List<CarEntity> retrieveCarsFilteredByCarMakeAndModel(String carModelMake, String carModelName) {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.model.modelMake = ?1 AND c.model.modelName = ?2")
                .setParameter(1, carModelMake)
                .setParameter(2, carModelName);
        List<CarEntity> cars = query.getResultList();
        return cars;
    }

    @Override
    public boolean isPotential(Long carID, Date startDateTime, Date endDateTime) {
        //same outlet
        //diff outlet +/- 2hrs
        return true;
    }

    @Override
    public CarEntity retrievePotentialCarOfCategoryOfOutlet(String carCategoryName, String outletAddress, Date startDateTime, Date endDateTime) {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.model.category.categoryName = ?1 AND c.currOutlet.address = ?2")
                .setParameter(1, carCategoryName)
                .setParameter(2, outletAddress);
        List<CarEntity> cars = query.getResultList();
        List<CarEntity> potentialCars = new ArrayList<>();
        for (CarEntity car : cars) {
            if (isPotential(car.getCarID(), startDateTime, endDateTime)) {
                potentialCars.add(car);
            }
        }
        return potentialCars.get(0);
    }
    
    @Override
    public CarEntity retrievePotentialCarOfCategoryOfOtherOutlet(String carCategoryName, String outletAddress, Date startDateTime, Date endDateTime) {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.model.category.categoryName = ?1 AND c.currOutlet.address != ?2")
                .setParameter(1, carCategoryName)
                .setParameter(2, outletAddress);
        List<CarEntity> cars = query.getResultList();
        List<CarEntity> potentialCars = new ArrayList<>();
        for (CarEntity car : cars) {
            if (isPotential(car.getCarID(), startDateTime, endDateTime)) {
                potentialCars.add(car);
            }
        }
        return potentialCars.get(0);
    }

}
