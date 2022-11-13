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
import javax.ejb.Local;
import javax.persistence.PersistenceException;
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
@Local
public interface CarSessionBeanLocal {

    public Long createNewCar(CarEntity car, String modelName, String outletAddress) throws CarModelNotFoundException, CarModelDisabledException, PersistenceException, CarLicensePlateExistsException, UnknownPersistenceException, InputDataValidationException;

    public CarEntity retrieveCarByCarID(Long carID) throws CarNotFoundException;
    
    public CarEntity retrieveCarByCarLicensePlateNumber(String licensePlateNumber) throws CarNotFoundException;
       
    public List<CarEntity> retrieveAllCars();

    public void updateCar(CarEntity car, String modelName, String outletAddress) throws CarModelNotFoundException;

    public void deleteCar(Long carID) throws DeleteCarException, CarNotFoundException;

    public List<CarEntity> retrieveAllCarsOfCarModel(String carModelName);

    public List<CarEntity> retrieveAllCarsOfOutlet(String outletAddress);

    public void pickUpCar(ReservationEntity reservation);
    
    public void returnCar(String reservationCode);
    
    public List<CarCategoryEntity> doSearchCar(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet);
        
    public List<CarEntity> retrieveAllCarsOfCarCategory(String carCategoryName);

    public void allocateCarToReservation(Long carID, Long reservationID) throws ReservationNotFoundException, CarNotFoundException;
    
    public List<CarEntity> retrieveCarsFilteredByCarCategory(String carCategoryName);

    public List<CarEntity> retrieveCarsFilteredByCarMakeAndModel(String carModelMake, String carModelName);

    public CarEntity retrievePotentialCarOfCategoryOfOutlet(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet, CarCategoryEntity carCategory);

    public CarEntity retrievePotentialCarOfCategoryOfOtherOutlet(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet, CarCategoryEntity carCategory);

    public void updateCarEntity(CarEntity car);

    

}
