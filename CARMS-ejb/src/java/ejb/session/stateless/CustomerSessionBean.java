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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author hanyang
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    public CustomerSessionBean() {
    }

    @Override
    public Long createNewCustomer(CustomerEntity customer) {
        em.persist(customer);
        em.flush();

        return customer.getCustomerID();
    }

    @Override
    public CustomerEntity retrieveCustomerByID(Long customerID) {
        CustomerEntity customer = em.find(CustomerEntity.class, customerID);
        customer.getReservations().size();
        return customer;
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
    public void deleteCustomer(Long customerID) {
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

}
