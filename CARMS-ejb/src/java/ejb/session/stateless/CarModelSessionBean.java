/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.CarModelEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author stonley
 */
@Stateless
public class CarModelSessionBean implements CarModelSessionBeanRemote, CarModelSessionBeanLocal {

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBeanLocal;

    @EJB(name = "CarCategorySessionBeanLocal")
    private CarCategorySessionBeanLocal carCategorySessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    

    @Override
    public Long createNewCarModel(CarModelEntity carModel, String carCategoryName) {
        CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(carCategoryName);
        carModel.setCategory(carCategory);
        em.persist(carModel);
        em.flush();
        
        return carModel.getCarModelID();
    }
    
    @Override
    public CarModelEntity retrieveCarModelByCarModelID(Long carModelID) {
        CarModelEntity carModel = em.find(CarModelEntity.class, carModelID);
        //carModel.getXX().size();
        return carModel;
    }
    
    @Override
    public CarModelEntity retrieveCarModelByCarModelName(String carModelName) {
        Query query = em.createQuery("SELECT c FROM CarModelEntity c WHERE c.modelName = ?1")
                .setParameter(1, carModelName);
        CarModelEntity carModel = (CarModelEntity) query.getSingleResult();
        //carModel.getXX().size();
        return carModel;
    }
    
    @Override
    public List<CarModelEntity> retrieveAllCarModels() {
        Query query = em.createQuery("SELECT c FROM CarModelEntity c ORDER BY c.category.categoryName ASC, c.modelMake ASC, c.modelName ASC");
        List<CarModelEntity> carModels = query.getResultList();
        return carModels;
    }
    
    @Override
    public void updateCarModel(CarModelEntity carModel, String categoryName) {
        CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(categoryName);
        carModel.setCategory(carCategory);
        em.merge(carModel);
    }
    
    @Override
    public void deleteCarModel(String carModelName) {
        CarModelEntity carModel = retrieveCarModelByCarModelName(carModelName);
        
        List<CarEntity> cars = carSessionBeanLocal.retrieveAllCarsOfCarModel(carModelName);
        
        for (CarEntity car : cars) {
            car.setModel(null);
        }
                
        em.remove(carModel);
    }

    
}
