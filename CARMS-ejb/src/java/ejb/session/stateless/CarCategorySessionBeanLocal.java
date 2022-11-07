/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import javax.ejb.Local;

/**
 *
 * @author hanyang
 */
@Local
public interface CarCategorySessionBeanLocal {

    public Long createNewCarCategory(CarCategoryEntity carCategory);

    public CarCategoryEntity retrieveCarCategoryByCarCategoryID(Long carCategoryID);
    
    public CarCategoryEntity retrieveCarCategoryByCarCategoryName(String carCategoryName);

    public void deleteCarCategory(Long carCategoryID);
    
}
