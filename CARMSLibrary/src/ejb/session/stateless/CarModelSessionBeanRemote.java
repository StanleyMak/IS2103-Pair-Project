/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarModelEntity;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CarCategoryNotFoundException;
import util.exception.CarModelNameExistsException;
import util.exception.CarModelNotFoundException;
import util.exception.DeleteCarModelException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author stonley
 */
@Remote
public interface CarModelSessionBeanRemote {

    public Long createNewCarModel(CarModelEntity carModel, String carCategoryName) throws CarModelNameExistsException, CarCategoryNotFoundException, UnknownPersistenceException, InputDataValidationException;

    public CarModelEntity retrieveCarModelByCarModelID(Long carModelID) throws CarModelNotFoundException;

    public CarModelEntity retrieveCarModelByCarModelName(String carModelName) throws CarModelNotFoundException;

    public List<CarModelEntity> retrieveAllCarModels();

    public List<CarModelEntity> retrieveCarModelsOfCarCategory(String carCategoryName);

    public void updateCarModel(CarModelEntity carModel, String categoryName) throws CarModelNotFoundException, CarCategoryNotFoundException;

    public void deleteCarModel(String carModelName) throws CarModelNotFoundException, DeleteCarModelException;


}
