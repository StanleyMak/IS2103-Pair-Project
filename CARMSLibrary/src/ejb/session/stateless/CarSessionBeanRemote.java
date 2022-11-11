/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarEntity;
import entity.OutletEntity;
import entity.ReservationEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CarModelDisabledException;
import util.exception.CarModelNotFoundException;
import util.exception.DeleteCarException;

/**
 *
 * @author stonley
 */
@Remote
public interface CarSessionBeanRemote {

    public Long createNewCar(CarEntity car, String modelName, String outletAddress) throws CarModelNotFoundException, CarModelDisabledException;

    public CarEntity retrieveCarByCarID(Long carID);

    public CarEntity retrieveCarByCarLicensePlateNumber(String licensePlateNumber);

    public List<CarEntity> retrieveAllCars();

    public void updateCar(CarEntity car, String modelName, String outletAddress) throws CarModelNotFoundException;

    public void deleteCar(Long carID) throws DeleteCarException;

    public List<CarEntity> retrieveAllCarsOfCarModel(String carModelName);

    public List<CarEntity> retrieveAllCarsOfOutlet(String outletAddress);

    public void pickUpCar(ReservationEntity reservation);

    public void returnCar(String reservationCode);
    
    public List<CarEntity> doSearchCar(Date pickupDateTime, Date returnDateTime, OutletEntity pickupOutlet, OutletEntity returnOutlet);
    
    public List<CarEntity> retrieveAllCarsOfCarCategory(String carCategoryName);
    
}
