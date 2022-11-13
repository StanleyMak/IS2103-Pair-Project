/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.RentalRateEntity;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RentalRateTypeEnum;
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
@Stateless
public class RentalRateSessionBean implements RentalRateSessionBeanRemote, RentalRateSessionBeanLocal {

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "CarCategorySessionBeanLocal")
    private CarCategorySessionBeanLocal carCategorySessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RentalRateSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewRentalRate(RentalRateEntity rentalRate, String categoryName) throws CarCategoryNotFoundException, PersistenceException, RentalRateNameExistsException, UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<RentalRateEntity>> constraintViolations = validator.validate(rentalRate);

        if (constraintViolations.isEmpty()) {
            try {
                CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(categoryName);
                rentalRate.setCarCategory(carCategory);
                
                em.persist(rentalRate);
                em.flush();
                return rentalRate.getRentalRateID();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new RentalRateNameExistsException("Rental Rate Name Exists");
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
    public List<RentalRateEntity> retrieveAllRentalRates() {
        Query query = em.createQuery("SELECT r FROM RentalRateEntity r ORDER BY r.carCategory.categoryName ASC, r.startDate ASC, r.endDate ASC");
        List<RentalRateEntity> rentalRates = query.getResultList();
        return rentalRates;
    }

    @Override
    public RentalRateEntity retrieveRentalRateByRentalRateID(Long rentalRateID) throws RentalRateNotFoundException {
        
        RentalRateEntity rentalRate = em.find(RentalRateEntity.class, rentalRateID);
        if (rentalRate != null) {
            return rentalRate;
        } else {
            throw new RentalRateNotFoundException("Rental rate does not exist");
        }
        //rentalRate.getXX().size();
    }

    @Override
    public RentalRateEntity retrieveRentalRateByRentalRateName(String rentalRateName) throws RentalRateNotFoundException {
        Query query = em.createQuery("SELECT r FROM RentalRateEntity r WHERE r.rentalName = ?1")
                .setParameter(1, rentalRateName);
        RentalRateEntity rentalRate = (RentalRateEntity) query.getSingleResult();
        
        if (rentalRate != null) {
            return rentalRate; 
        } else {
            throw new RentalRateNotFoundException("Rental rate does not exist");
        }
    }

    public List<RentalRateEntity> retrieveRentalRatesOfCarCategory(String carCategoryName) {
        Query query = em.createQuery("SELECT r FROM RentalRateEntity r WHERE r.carCategory.categoryName = ?1")
                .setParameter(1, carCategoryName);
        List<RentalRateEntity> rentalRates = query.getResultList();
        return rentalRates;
    }

    @Override
    public void updateRentalRate(RentalRateEntity rentalRate, String categoryName) throws CarCategoryNotFoundException {
        try {
            CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(categoryName);
            rentalRate.setCarCategory(carCategory);
            em.merge(rentalRate);
        } catch (CarCategoryNotFoundException e) {
            throw new CarCategoryNotFoundException("Car Category Name " + categoryName + "does not exist!\n");
        }

    }

    @Override
    public void deleteRentalRate(Long rentalRateID) throws DeleteRentalRateException, RentalRateNotFoundException {
        
        RentalRateEntity rentalRate = retrieveRentalRateByRentalRateID(rentalRateID);

        //dissociate
        em.remove(rentalRate);

        if (reservationSessionBeanLocal.retrieveReservationsOfRentalRateID(rentalRateID).isEmpty()) {
            em.remove(rentalRate);
        } else {
            rentalRate.setIsDisabled(Boolean.TRUE);
            throw new DeleteRentalRateException("Rental Rate ID " + rentalRateID + " is associated with existing reservation(s) and cannot be deleted!");
        }
    }

    @Override
    public double computeCheapestRentalRateFee(Date startDateTime, Date endDateTime, String carCategoryName) {
        double total = 0;
        Date currDate = startDateTime;

        LocalDateTime cycleStartLocal = LocalDateTime.ofInstant(currDate.toInstant(), ZoneId.systemDefault());;
        LocalDateTime cycleEndLocal = cycleStartLocal.plusDays(1);

        Date cycleStart = Date.from(cycleStartLocal.atZone(ZoneId.systemDefault()).toInstant());
        Date cycleEnd = Date.from(cycleEndLocal.atZone(ZoneId.systemDefault()).toInstant());

        while (cycleStart.compareTo(endDateTime) < 0) {

            if (cycleEnd.after(endDateTime)) {
                cycleEnd = endDateTime;
            }

            total += retrieveCheapestRentalRateFeeForCurrentDay(cycleStart, cycleEnd, carCategoryName);

            cycleStartLocal = cycleStartLocal.plusDays(1);
            cycleEndLocal = cycleEndLocal.plusDays(1);

            cycleStart = Date.from(cycleStartLocal.atZone(ZoneId.systemDefault()).toInstant());
            cycleEnd = Date.from(cycleEndLocal.atZone(ZoneId.systemDefault()).toInstant());

        }

        return total;
    }

    //check if returns double or returns rentalRate
    private double retrieveCheapestRentalRateFeeForCurrentDay(Date cycleStart, Date cycleEnd, String carCategoryName) {
        System.out.println("Enter Method 2");
        List<RentalRateEntity> rentalRatesForCategory = retrieveRentalRatesOfCarCategory(carCategoryName);
        List<RentalRateEntity> rentalRatesForCategoryForCurrentDay = new ArrayList<>();

        for (RentalRateEntity rentalRate : rentalRatesForCategory) {
            if ((cycleStart.after(rentalRate.getStartDate()) && cycleStart.before(rentalRate.getEndDate()))
                    || (cycleEnd.after(rentalRate.getStartDate()) && cycleEnd.before(rentalRate.getEndDate()))
                    || (cycleStart.before(rentalRate.getStartDate()) && cycleEnd.after(rentalRate.getEndDate()))
                    || (cycleStart.equals(rentalRate.getStartDate()) && cycleEnd.equals(rentalRate.getEndDate()))) {
                rentalRatesForCategoryForCurrentDay.add(rentalRate);
            }
        }

        List<RentalRateEntity> isPromo = new ArrayList<>();
        List<RentalRateEntity> isPeak = new ArrayList<>();
        List<RentalRateEntity> isDefault = new ArrayList<>();

        for (RentalRateEntity rentalRate : rentalRatesForCategoryForCurrentDay) {
            if (rentalRate.getRentalRateType().equals(RentalRateTypeEnum.PROMO)) {
                isPromo.add(rentalRate);
            }
            if (rentalRate.getRentalRateType().equals(RentalRateTypeEnum.PEAK)) {
                isPeak.add(rentalRate);
            }
            if (rentalRate.getRentalRateType().equals(RentalRateTypeEnum.DEFAULT)) {
                isDefault.add(rentalRate);
            }
        }

        if (!isPromo.isEmpty()) {
            return findMin(isPromo).getRatePerDay();
        } else if (!isPeak.isEmpty()) {
            return findMin(isPeak).getRatePerDay();
        } else {
            return findMin(isDefault).getRatePerDay();
        }
    }

    private RentalRateEntity findMin(List<RentalRateEntity> rr) {
        RentalRateEntity min = rr.get(0);
        for (RentalRateEntity r : rr) {
            if (r.getRatePerDay() < min.getRatePerDay()) {
                min = r;
            }
        }
        return min;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RentalRateEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
