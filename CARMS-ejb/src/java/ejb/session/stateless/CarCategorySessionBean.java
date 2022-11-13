/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CarCategoryNameExistsException;
import util.exception.CarCategoryNotFoundException;
import util.exception.DeleteCarCategoryException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author hanyang
 */
@Stateless
public class CarCategorySessionBean implements CarCategorySessionBeanRemote, CarCategorySessionBeanLocal {

    @EJB(name = "RentalRateSessionBeanLocal")
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

    @EJB(name = "CarModelSessionBeanLocal")
    private CarModelSessionBeanLocal carModelSessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CarCategorySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewCarCategory(CarCategoryEntity carCategory) throws CarCategoryNameExistsException, UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<CarCategoryEntity>> constraintViolations = validator.validate(carCategory);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(carCategory);
                em.flush();

                return carCategory.getCarCategoryID();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CarCategoryNameExistsException("Car Category Exists");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public CarCategoryEntity retrieveCarCategoryByCarCategoryID(Long carCategoryID) throws CarCategoryNotFoundException {
        CarCategoryEntity carCategory = em.find(CarCategoryEntity.class, carCategoryID);

        if (carCategory != null) {
            return carCategory;
        } else {
            throw new CarCategoryNotFoundException("Car Category ID " + carCategoryID + " does not exist!");
        }

    }

    @Override
    public CarCategoryEntity retrieveCarCategoryByCarCategoryName(String carCategoryName) throws CarCategoryNotFoundException {
        Query query = em.createQuery("SELECT c FROM CarCategoryEntity c WHERE c.categoryName = ?1")
                .setParameter(1, carCategoryName);

        try {
            return (CarCategoryEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CarCategoryNotFoundException("Car Category Name " + carCategoryName + " does not exist!");
        }

    }

    @Override
    public void deleteCarCategory(Long carCategoryID) throws CarCategoryNotFoundException, DeleteCarCategoryException {
        CarCategoryEntity carCategory = retrieveCarCategoryByCarCategoryID(carCategoryID);

        if (carModelSessionBeanLocal.retrieveCarModelsOfCarCategory(carCategory.getCategoryName()).isEmpty()
                && rentalRateSessionBeanLocal.retrieveRentalRatesOfCarCategory(carCategory.getCategoryName()).isEmpty()) {
            em.remove(carCategory);
        } else {
            throw new DeleteCarCategoryException("Car Category ID " + carCategoryID + " is associated with existing car model(s) or rental rate(s) and cannot be deleted!");
        }

    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CarCategoryEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
    
    @Override 
    public List<CarCategoryEntity> retrieveAllCarCategory() {
        Query query = em.createQuery("SELECT cc FROM CarCategoryEntity cc");
        List<CarCategoryEntity> carCategories = query.getResultList();
        return carCategories; 
    }

}
