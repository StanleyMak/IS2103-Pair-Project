/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.OwnCustomerEntity;
import entity.ReservationEntity;
import java.util.ArrayList;
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
import util.exception.CustomerEmailExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author hanyang
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CustomerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewCustomer(CustomerEntity customer) throws CustomerEmailExistsException, UnknownPersistenceException, InputDataValidationException, PersistenceException {
        
        Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(customer);

        if (constraintViolations.isEmpty()) {
            try {
                em.persist(customer);
                em.flush();

                return customer.getCustomerID();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CustomerEmailExistsException("Customer Email Exists");
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
    public CustomerEntity retrieveCustomerByID(Long customerID) throws CustomerNotFoundException {
        
        CustomerEntity customer = em.find(CustomerEntity.class, customerID);
        customer.getReservations().size();
        
        if (customer != null) {
            return customer;
        } else {
            throw new CustomerNotFoundException("Customer " + customerID + "does not exist");
        } 
    }

    public CustomerEntity retrieveCustomerByCustomerEmail(String email) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.email = ?1")
                .setParameter(1, email);

        try {
            CustomerEntity customer = (CustomerEntity) query.getSingleResult();
            customer.getReservations().size();
            return customer;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer email " + email + "does not exist!");
        }
    }

    @Override
    public OwnCustomerEntity retrieveOwnCustomerByOwnCustomerEmail(String email) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.email = ?1")
                .setParameter(1, email);

        try {
            OwnCustomerEntity customer = (OwnCustomerEntity) query.getSingleResult();
            customer.getReservations().size();
            return customer;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer email " + email + "does not exist!");
        }
    }

    @Override
    public void deleteCustomer(Long customerID) throws CustomerNotFoundException {
        CustomerEntity customer = retrieveCustomerByID(customerID);
        //dissociate
        em.remove(customer);
    }

    @Override
    public OwnCustomerEntity customerLogin(String email, String password) throws InvalidLoginCredentialException {
        try {
            OwnCustomerEntity customer = retrieveOwnCustomerByOwnCustomerEmail(email);

            if (password.equals(customer.getPassword())) {
                customer.getReservations().size();
                return customer;
            } else {
                throw new InvalidLoginCredentialException("Invalid email or password");
            }
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid email or password");
        }
    }

    @Override
    public List<CustomerEntity> retrieveAllCustomers() {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c");
        List<CustomerEntity> customers = query.getResultList();
        for (CustomerEntity cus : customers) {
            cus.getReservations().size();
        }
        return customers;
    }

    private Boolean containsReservationID(CustomerEntity customer, Long reservationID) {
        List<ReservationEntity> reservations = customer.getReservations();
        for (ReservationEntity reservation : reservations) {
            if (reservation.getReservationID() == reservationID) {
                return true;
            }
        }
        return false;
    }

    //review return type
    @Override
    public CustomerEntity retrieveCustomerOfReservationID(Long reservationID) {
        List<CustomerEntity> customers = retrieveAllCustomers();
        for (CustomerEntity customer : customers) {
            if (containsReservationID(customer, reservationID)) {
                return customer;
            }
        }
        return new CustomerEntity();
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CustomerEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
    
    @Override
    public List<CustomerEntity> retrieveCustomersOfPartnerUsername(String username) {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.partner.username = ?1")
                .setParameter(1, username);
        List<CustomerEntity> customers = query.getResultList();
        for (CustomerEntity cus : customers) {
            cus.getReservations().size();
        }
        return customers;
    }

}
