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
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.StatusEnum;
import util.exception.CarLicensePlateExistsException;
import util.exception.CarModelDisabledException;
import util.exception.CarModelNotFoundException;
import util.exception.CarNotFoundException;
import util.exception.DeleteCarException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

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

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CarSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewCar(CarEntity car, String modelName, String outletAddress) throws CarModelNotFoundException, CarModelDisabledException, PersistenceException, CarLicensePlateExistsException, UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<CarEntity>> constraintViolations = validator.validate(car);

        if (constraintViolations.isEmpty()) {
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
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CarLicensePlateExistsException("Car with this license plate exists");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

//        try {
//            CarModelEntity carModel = carModelSessionBeanLocal.retrieveCarModelByCarModelName(modelName);
//            if (!carModel.getIsDisabled()) {
//                car.setModel(carModel);
//
//                OutletEntity outlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(outletAddress);
//                car.setCurrOutlet(outlet);
//
//                em.persist(car);
//                em.flush();
//            } else {
//                throw new CarModelDisabledException("Car Model " + modelName + " is disabled");
//            }
//
//            return car.getCarID();
//        } catch (CarModelNotFoundException e) {
//            throw new CarModelNotFoundException("Car Model Name " + modelName + " does not exist!\n");
//        }
    }

    @Override
    public CarEntity retrieveCarByCarID(Long carID) throws CarNotFoundException {
        
        CarEntity car = em.find(CarEntity.class, carID);
        if (car != null) {
            return car;
        } else {
            throw new CarNotFoundException("Car " + carID + "does not exist!"); 
        }
    }

    @Override
    public CarEntity retrieveCarByCarLicensePlateNumber(String licensePlateNumber) throws CarNotFoundException {
        Query query = em.createQuery("SELECT c FROM CarEntity c WHERE c.licensePlateNumber = ?1")
                .setParameter(1, licensePlateNumber);
        CarEntity car = (CarEntity) query.getSingleResult();
        
        if (car != null) {
        //car.getXX().size();
            return car;
        } else {
            throw new CarNotFoundException("Car " + licensePlateNumber + "does not exist");
        }
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
    public void updateCarEntity(CarEntity car) {
        em.merge(car);
    }

    @Override
    public void deleteCar(Long carID) throws DeleteCarException, CarNotFoundException {
        
        CarEntity car = retrieveCarByCarID(carID); 
        if (reservationSessionBeanLocal.retrieveReservationsOfCarID(carID).isEmpty()) {
            em.remove(car);
        } else {
            car.setStatus(StatusEnum.DISABLED);
            throw new DeleteCarException("Car ID " + carID + " is associated with existing reservation(s) and cannot be deleted!");
        }
    }

    //LOOK AT ALLOCATE CAR, CAR SHOULD HAVE BEEN ASSOCIATED BY NOW
    @Override
    public void pickUpCar(ReservationEntity reservation) {
        //
        CarEntity pickUpCar = reservation.getCar();

        // update car status to rented out
        pickUpCar.setStatus(StatusEnum.ON_RENTAL);
        // update car location to pick up location
        pickUpCar.setCurrOutlet(reservation.getPickUpOutlet());

        em.merge(pickUpCar);
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
        em.merge(returnCar);
    }

    @Override
    public List<CarCategoryEntity> doSearchCar(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet) {

        List<CarCategoryEntity> availableCategories = new ArrayList<>();

        List<CarCategoryEntity> allCategories = carCategorySessionBeanLocal.retrieveAllCarCategory();

        // retrieve reservations of category and count num reservations per category
        for (CarCategoryEntity cc : allCategories) {

            int numRes = getNumIntersectedReservationsForCategory(pickupDateTime, returnDateTime, cc, pickupOutlet, returnOutlet);
            List<CarEntity> availableCarsByCategory = retrieveCarsFilteredByCarCategory(cc.getCategoryName());

            List<CarEntity> availableCars = new ArrayList<>();

            for (CarEntity car : availableCarsByCategory) {
                if (!car.getStatus().equals(StatusEnum.DISABLED) && !car.getStatus().equals(StatusEnum.REPAIR)) {
                    availableCars.add(car);
                }
            }

            if (numRes < availableCars.size()) {
                availableCategories.add(cc);
            }
        }

        return availableCategories;
    }

    @Override
    public CarEntity retrievePotentialCarOfCategoryOfOutlet(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet, CarCategoryEntity carCategory) {

        List<CarEntity> availableCarsByCategory = retrieveCarsFilteredByCarCategory(carCategory.getCategoryName());

        List<CarEntity> availableCars = new ArrayList<>();

        //if any reservation of a car intersects with my window, it is not a potential
        for (CarEntity car : availableCarsByCategory) {
            List<ReservationEntity> carReservations = reservationSessionBeanLocal.retrieveReservationsOfCarID(car.getCarID());
            int numRes = 0;

            LocalDateTime transitStartDateTime = LocalDateTime.ofInstant(pickupDateTime.toInstant(), ZoneId.systemDefault());
            LocalDateTime transitEndDateTime = LocalDateTime.ofInstant(returnDateTime.toInstant(), ZoneId.systemDefault());
            // factor in transit duration (2 hours)

            //Local Date Time for transit
            transitEndDateTime = transitEndDateTime.minusHours(2);
            transitStartDateTime = transitStartDateTime.plusHours(2);

            for (ReservationEntity res : carReservations) {

                //Local Date Time for res start/end dates
                LocalDateTime resStartDateTime = LocalDateTime.ofInstant(pickupDateTime.toInstant(), ZoneId.systemDefault());
                LocalDateTime resEndDateTime = LocalDateTime.ofInstant(returnDateTime.toInstant(), ZoneId.systemDefault());

                if (res.getStartDateTime().after(pickupDateTime)) {
                    if (res.getPickUpOutlet().equals(returnOutlet)) {
                        if (res.getStartDateTime().before(returnDateTime)) {
                            numRes++;
                        }
                    } else {
                        if (resStartDateTime.isBefore(transitEndDateTime)) {
                            numRes++;
                        }
                    }
                } else if (res.getEndDateTime().before(returnDateTime)) {
                    if (res.getReturnOutlet().equals(pickupOutlet)) {
                        if (res.getEndDateTime().after(pickupDateTime)) {
                            numRes++;
                        }
                    } else {
                        if (resEndDateTime.isAfter(transitStartDateTime)) {
                            numRes++;
                        }
                    }
                }
            }

            if (numRes > 0) {
                break;
            }

            if (!car.getStatus().equals(StatusEnum.DISABLED) && !car.getStatus().equals(StatusEnum.REPAIR)) {
                availableCars.add(car);
            }
        }

        for (CarEntity car : availableCars) {
            if (!(car.getCurrOutlet().equals(pickupOutlet))) {
                availableCars.remove(car);
            }
        }

        return availableCars.get(0);
    }

    @Override
    public CarEntity retrievePotentialCarOfCategoryOfOtherOutlet(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet, CarCategoryEntity carCategory) {

        List<CarEntity> availableCarsByCategory = retrieveCarsFilteredByCarCategory(carCategory.getCategoryName());

        List<CarEntity> availableCars = new ArrayList<>();

        //if any reservation of a car intersects with my window, it is not a potential
        for (CarEntity car : availableCarsByCategory) {
            List<ReservationEntity> carReservations = reservationSessionBeanLocal.retrieveReservationsOfCarID(car.getCarID());
            int numRes = 0;

            LocalDateTime transitStartDateTime = LocalDateTime.ofInstant(pickupDateTime.toInstant(), ZoneId.systemDefault());
            LocalDateTime transitEndDateTime = LocalDateTime.ofInstant(returnDateTime.toInstant(), ZoneId.systemDefault());
            // factor in transit duration (2 hours)

            //Local Date Time for transit
            transitEndDateTime = transitEndDateTime.minusHours(2);
            transitStartDateTime = transitStartDateTime.plusHours(2);

            for (ReservationEntity res : carReservations) {

                //Local Date Time for res start/end dates
                LocalDateTime resStartDateTime = LocalDateTime.ofInstant(pickupDateTime.toInstant(), ZoneId.systemDefault());
                LocalDateTime resEndDateTime = LocalDateTime.ofInstant(returnDateTime.toInstant(), ZoneId.systemDefault());

                if (res.getStartDateTime().after(pickupDateTime)) {
                    if (res.getPickUpOutlet().equals(returnOutlet)) {
                        if (res.getStartDateTime().before(returnDateTime)) {
                            numRes++;
                        }
                    } else {
                        if (resStartDateTime.isBefore(transitEndDateTime)) {
                            numRes++;
                        }
                    }
                } else if (res.getEndDateTime().before(returnDateTime)) {
                    if (res.getReturnOutlet().equals(pickupOutlet)) {
                        if (res.getEndDateTime().after(pickupDateTime)) {
                            numRes++;
                        }
                    } else {
                        if (resEndDateTime.isAfter(transitStartDateTime)) {
                            numRes++;
                        }
                    }
                }
            }

            if (numRes > 0) {
                break;
            }

            if (!car.getStatus().equals(StatusEnum.DISABLED) && !car.getStatus().equals(StatusEnum.REPAIR)) {
                availableCars.add(car);
            }
        }

        for (CarEntity car : availableCars) {
            if (car.getCurrOutlet().equals(pickupOutlet)) {
                availableCars.remove(car);
            }
        }

        return availableCars.get(0);
    }

    private int getNumIntersectedReservationsForCategory(Date newStartDate, Date newEndDate, CarCategoryEntity cc, OutletEntity pickUpOutlet, OutletEntity returnOutlet) {

        int numRes = 0;
        List<ReservationEntity> allReservations = reservationSessionBeanLocal.retrieveReservationsByCategory(cc.getCategoryName());

        LocalDateTime transitStartDateTime = LocalDateTime.ofInstant(newStartDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime transitEndDateTime = LocalDateTime.ofInstant(newEndDate.toInstant(), ZoneId.systemDefault());
        // factor in transit duration (2 hours)

        //Local Date Time for transit
        transitEndDateTime = transitEndDateTime.minusHours(2);
        transitStartDateTime = transitStartDateTime.plusHours(2);

        for (ReservationEntity res : allReservations) {

            //Local Date Time for res start/end dates
            LocalDateTime resStartDateTime = LocalDateTime.ofInstant(newStartDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime resEndDateTime = LocalDateTime.ofInstant(newEndDate.toInstant(), ZoneId.systemDefault());

            if (res.getStartDateTime().after(newStartDate)) {
                if (res.getPickUpOutlet().equals(returnOutlet)) {
                    if (res.getStartDateTime().before(newEndDate)) {
                        numRes++;
                    }
                } else {
                    if (resStartDateTime.isBefore(transitEndDateTime)) {
                        numRes++;
                    }
                }
            } else if (res.getEndDateTime().before(newEndDate)) {
                if (res.getReturnOutlet().equals(pickUpOutlet)) {
                    if (res.getEndDateTime().after(newStartDate)) {
                        numRes++;
                    }
                } else {
                    if (resEndDateTime.isAfter(transitStartDateTime)) {
                        numRes++;
                    }
                }
            }
        }

        return numRes;
    }

//    @Override
//    public void doReserveCar(ReservationEntity reservation, String carCategoryName) {
//        List<ReservationEntity> reservationsForCat = reservationSessionBeanLocal.retrieveReservationsByCategory(carCategoryName);
//        CarCategoryEntity cat = new CarCategoryEntity();
//        cat.setCategoryName(carCategoryName);
//        reservation.setCarCategory(carCategory);
//
//    }
    @Override
    public void allocateCarToReservation(Long carID, Long reservationID) throws ReservationNotFoundException, CarNotFoundException {
        
        CarEntity car = retrieveCarByCarID(carID);
        
        ReservationEntity reservation = null;
        try {
            reservation = reservationSessionBeanLocal.retrieveReservationByID(reservationID);
            reservation.setCar(car);
        } catch (ReservationNotFoundException e) {
            throw new ReservationNotFoundException("Reservation " + reservationID + " not found");
        }

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

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CarEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
