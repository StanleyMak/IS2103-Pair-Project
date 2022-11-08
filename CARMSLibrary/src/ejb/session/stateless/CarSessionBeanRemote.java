/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarEntity;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author stonley
 */
@Remote
public interface CarSessionBeanRemote {
    
    public Long createNewCar(CarEntity car);

    public CarEntity retrieveCarByCarID(Long carID);

    public CarEntity retrieveCarByCarLicensePlateNumber(String licensePlateNumber);

    public List<CarEntity> retrieveAllCars();

    public void updateCar(CarEntity car);
    
    public void deleteCar(Long carID);
    

    public void pickUpCar(String email, String reservationCode);
    public void returnCar(String email, String reservationCode);
}
