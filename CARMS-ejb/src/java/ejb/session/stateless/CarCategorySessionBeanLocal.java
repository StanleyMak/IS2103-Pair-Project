/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CarCategoryNameExistsException;
import util.exception.CarCategoryNotFoundException;
import util.exception.DeleteCarCategoryException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author hanyang
 */
@Local
public interface CarCategorySessionBeanLocal {

    public Long createNewCarCategory(CarCategoryEntity carCategory) throws CarCategoryNameExistsException, UnknownPersistenceException, InputDataValidationException;

    public CarCategoryEntity retrieveCarCategoryByCarCategoryID(Long carCategoryID) throws CarCategoryNotFoundException;

    public CarCategoryEntity retrieveCarCategoryByCarCategoryName(String carCategoryName) throws CarCategoryNotFoundException;

    public void deleteCarCategory(Long carCategoryID) throws CarCategoryNotFoundException, DeleteCarCategoryException;

    public List<CarCategoryEntity> retrieveAllCarCategory();
    
    
}
