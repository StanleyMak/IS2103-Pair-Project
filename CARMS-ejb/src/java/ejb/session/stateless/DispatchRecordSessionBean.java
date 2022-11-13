/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.DispatchRecordEntity;
import entity.EmployeeEntity;
import entity.OutletEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
import util.enumeration.StatusEnum;
import util.exception.CarCategoryNameExistsException;
import util.exception.DispatchRecordNameExistsException;
import util.exception.DispatchRecordNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author stonley
 */
@Stateless
public class DispatchRecordSessionBean implements DispatchRecordSessionBeanRemote, DispatchRecordSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public DispatchRecordSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Long createNewDispatchRecord(DispatchRecordEntity dispatchRecord) throws DispatchRecordNameExistsException, UnknownPersistenceException, InputDataValidationException, DispatchRecordNameExistsException {
        
        Set<ConstraintViolation<DispatchRecordEntity>> constraintViolations = validator.validate(dispatchRecord);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(dispatchRecord);
                em.flush();

                return dispatchRecord.getDispatchRecordID();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new DispatchRecordNameExistsException("Dispatch record exists");
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
        
//        try {
//            DispatchRecordEntity thisDispatchRecord = retrieveDispatchRecordByDispatchRecordName(dispatchRecord.getDispatchRecordName());
//            throw new DispatchRecordNameExistsException("Dispatch Record Already Exists");
//        } catch (DispatchRecordNotFoundException e) {
//            em.persist(dispatchRecord);
//            em.flush();
//
//            return dispatchRecord.getDispatchRecordID();
//        }

    }

    @Override
    public DispatchRecordEntity retrieveDisptachRecordByDispatchRecordID(Long dispatchRecordID) throws DispatchRecordNotFoundException {
        DispatchRecordEntity dispatchRecord = em.find(DispatchRecordEntity.class, dispatchRecordID);
        if (dispatchRecord != null) {
            return dispatchRecord;
        } else {
            throw new DispatchRecordNotFoundException("Dispatch Record Not Found");
        }

    }

    @Override
    public DispatchRecordEntity retrieveDispatchRecordByDispatchRecordName(String dispatchRecordName) throws DispatchRecordNotFoundException {
        Query query = em.createQuery("SELECT d FROM DispatchRecordEntity d WHERE d.dispatchRecordName = ?1")
                .setParameter(1, dispatchRecordName);
        try {
            return (DispatchRecordEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new DispatchRecordNotFoundException("Dispatch Record Name " + dispatchRecordName + " does not exist!");
        }
    }

    @Override
    public List<DispatchRecordEntity> retrieveDispatchRecordsForCurrentDayCurrentOutlet(Date date, OutletEntity outlet) throws ParseException {
        Query query = em.createQuery("SELECT d FROM DispatchRecordEntity WHERE d.outlet.address = ?1")
                .setParameter(1, outlet.getAddress());
        List<DispatchRecordEntity> dispatchRecords = query.getResultList();
        Date pickUpDate = new Date();
        Date dateOnly = new Date();
        for (DispatchRecordEntity dr : dispatchRecords) {
            try {
                pickUpDate = dateFormat.parse(dateFormat.format(dr.getPickUpTime()));
                dateOnly = dateFormat.parse(dateFormat.format(date));
            } catch (ParseException e) {
                throw new ParseException("Inalid Date/Time Format", 1); //wats the 1??
            }

            if (!pickUpDate.equals(date)) {
                dispatchRecords.remove(dr);
            }
        }
        return dispatchRecords;
    }

    @Override
    public void updateDispatchRecordAsCompleted(Long dispatchRecordID) throws DispatchRecordNotFoundException {
        try {
            DispatchRecordEntity dispatchRecord = retrieveDisptachRecordByDispatchRecordID(dispatchRecordID);
            dispatchRecord.setIsCompleted(Boolean.TRUE);
            dispatchRecord.getEmployee().setOnTransit(Boolean.FALSE);

            CarEntity car = dispatchRecord.getReservation().getCar();
            car.setStatus(StatusEnum.AVAILABLE);

            em.merge(dispatchRecord);
            em.merge(car);
        } catch (DispatchRecordNotFoundException e) {
            throw new DispatchRecordNotFoundException("Dispatch Record ID " + dispatchRecordID + " does not exist!");
        }
    }

    @Override
    public void assignTransitDriver(DispatchRecordEntity dr, EmployeeEntity emp) {
        dr.setEmployee(emp);
        emp.setOnTransit(Boolean.TRUE);
        em.merge(dr);
        em.merge(emp);
    }
//    no need delete
//    public void deleteDispatchRecord(Long dispatchRecordID) throws DispatchRecordNotFoundException {
//        DispatchRecordEntity dispatchRecord = retrieveDisptachRecordByDispatchRecordID(dispatchRecordID);
//        //dissociate
//
//        em.remove(dispatchRecord);
//    }
    
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<DispatchRecordEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
