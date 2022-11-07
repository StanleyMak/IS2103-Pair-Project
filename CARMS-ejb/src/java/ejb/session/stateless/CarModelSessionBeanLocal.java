/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarModelEntity;
import javax.ejb.Local;

/**
 *
 * @author stonley
 */
@Local
public interface CarModelSessionBeanLocal {

    public Long createNewCarModel(CarModelEntity carModel, String carCategoryName);

    public CarModelEntity retrieveCarModelByCarModelID(Long carModelID);

    public CarModelEntity retrieveCarModelByCarModel(String carModelModel);
    
}
