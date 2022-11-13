/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalRateEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import javax.persistence.PersistenceException;
import util.exception.CarCategoryNotFoundException;
import util.exception.DeleteRentalRateException;
import util.exception.InputDataValidationException;
import util.exception.RentalRateNameExistsException;
import util.exception.RentalRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author stonley
 */
@Remote
public interface RentalRateSessionBeanRemote {

    public Long createNewRentalRate(RentalRateEntity rentalRate, String categoryName) throws CarCategoryNotFoundException, PersistenceException, RentalRateNameExistsException, UnknownPersistenceException, InputDataValidationException;
    
    public List<RentalRateEntity> retrieveAllRentalRates();

    public RentalRateEntity retrieveRentalRateByRentalRateID(Long rentalRateID) throws RentalRateNotFoundException;

    public RentalRateEntity retrieveRentalRateByRentalRateName(String rentalRateName) throws RentalRateNotFoundException;

    public void updateRentalRate(RentalRateEntity rentalRate, String categoryName) throws CarCategoryNotFoundException;

    public List<RentalRateEntity> retrieveRentalRatesOfCarCategory(String carCategoryName);
    
    public void deleteRentalRate(Long rentalRateID) throws DeleteRentalRateException, RentalRateNotFoundException;
    
    public double computeCheapestRentalRateFee(Date startDateTime, Date endDateTime, String carCategoryName);
}
