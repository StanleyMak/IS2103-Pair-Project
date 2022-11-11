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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CarCategoryNotFoundException;
import util.exception.CarModelNameExistsException;
import util.exception.CarModelNameExistsException;
import util.exception.CarModelNotFoundException;
import util.exception.DeleteCarModelException;

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
    public Long createNewCarModel(CarModelEntity carModel, String carCategoryName) throws CarModelNameExistsException, CarCategoryNotFoundException {

        // ONLY CREATE WITH MODELS THAT ARE NOT DISABLED
        CarCategoryEntity carCategory = null;
        try {
            carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(carCategoryName);
        } catch (CarCategoryNotFoundException e) {
            throw new CarCategoryNotFoundException("Car Category Not Found");
        }

        try {
            CarModelEntity thisCarModel = retrieveCarModelByCarModelName(carModel.getModelName());
            throw new CarModelNameExistsException("Car Model Name: " + carModel.getModelName() + " already exists");
        } catch (CarModelNotFoundException e) {
            carModel.setCategory(carCategory);
            em.persist(carModel);
            em.flush();
        }

        return carModel.getCarModelID();

    }

    @Override
    public CarModelEntity retrieveCarModelByCarModelID(Long carModelID) throws CarModelNotFoundException {

        CarModelEntity carModel = em.find(CarModelEntity.class, carModelID);
        if (carModel != null) {
            return carModel;
        } else {
            throw new CarModelNotFoundException("Car Model ID " + carModelID + " does not exist!");
        }

    }

    @Override
    public CarModelEntity retrieveCarModelByCarModelName(String carModelName) throws CarModelNotFoundException {
        Query query = em.createQuery("SELECT c FROM CarModelEntity c WHERE c.modelName = ?1")
                .setParameter(1, carModelName);
        CarModelEntity carModel = null;
        try {
            carModel = (CarModelEntity) query.getSingleResult(); // no result exception
            //carModel.getXX().size();
            return carModel;
        } catch (NoResultException e) {
            throw new CarModelNotFoundException("Car Model Name " + carModelName + " does not exist!");
        }

    }

    @Override
    public List<CarModelEntity> retrieveAllCarModels() {
        Query query = em.createQuery("SELECT c FROM CarModelEntity c ORDER BY c.category.categoryName ASC, c.modelMake ASC, c.modelName ASC");
        List<CarModelEntity> carModels = query.getResultList();
        return carModels;
    }

    public List<CarModelEntity> retrieveCarModelsOfCarCategory(String carCategoryName) {
        Query query = em.createQuery("SELECT c FROM CarModelEntity c WHERE c.category.categoryName = ?1")
                .setParameter(1, carCategoryName);
        List<CarModelEntity> carModels = query.getResultList();
        return carModels;
    }

    @Override
    public void updateCarModel(CarModelEntity carModel, String categoryName) throws CarModelNotFoundException, CarCategoryNotFoundException {
        CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(categoryName);
        try {
            if (carCategory == null) {
                throw new CarCategoryNotFoundException("Car Category Not Found");
            } else {
                carModel.setCategory(carCategory);
                em.merge(carModel);
            }
        } catch (CarCategoryNotFoundException e) {
            throw new CarCategoryNotFoundException("Car Category Not Found");
        }

    }
   

    @Override
    public void deleteCarModel(String carModelName) throws CarModelNotFoundException, DeleteCarModelException {

        CarModelEntity carModel = retrieveCarModelByCarModelName(carModelName);

        if (carSessionBeanLocal.retrieveAllCarsOfCarModel(carModelName).isEmpty()) {
            em.remove(carModel);
        } else {
            carModel.setIsDisabled(Boolean.TRUE);
            throw new DeleteCarModelException("Car Model Name " + carModelName + " is associated with existing car(s) and cannot be deleted!");
        }
    }

}
