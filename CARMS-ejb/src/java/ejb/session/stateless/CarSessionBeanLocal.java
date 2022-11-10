/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarEntity;
import entity.ReservationEntity;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author stonley
 */
@Local
public interface CarSessionBeanLocal {

    public Long createNewCar(CarEntity car, String modelName, String outletAddress);

    public CarEntity retrieveCarByCarID(Long carID);

    public CarEntity retrieveCarByCarLicensePlateNumber(String licensePlateNumber);

    public List<CarEntity> retrieveAllCars();

    public void updateCar(CarEntity car, String modelName, String outletAddress);

    public void deleteCar(Long carID);

    public List<CarEntity> retrieveAllCarsOfCarModel(String carModelName);

    public List<CarEntity> retrieveAllCarsOfOutlet(String outletAddress);

    public void pickUpCar(ReservationEntity reservation);
    
     public void returnCar(String reservationCode);
}
