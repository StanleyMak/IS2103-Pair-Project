/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.OwnCustomerEntity;
import java.util.List;
import javax.ejb.Remote;
import javax.persistence.PersistenceException;
import util.exception.CustomerEmailExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author hanyang
 */
@Remote
public interface CustomerSessionBeanRemote {
    
    public Long createNewCustomer(CustomerEntity customer) throws CustomerEmailExistsException, UnknownPersistenceException, InputDataValidationException, PersistenceException;

    public CustomerEntity retrieveCustomerByID(Long customerID) throws CustomerNotFoundException;

    public void deleteCustomer(Long customerID) throws CustomerNotFoundException;

    public OwnCustomerEntity customerLogin(String email, String password) throws InvalidLoginCredentialException;
    
    public CustomerEntity retrieveCustomerByCustomerEmail(String email) throws CustomerNotFoundException;

    public OwnCustomerEntity retrieveOwnCustomerByOwnCustomerEmail(String email) throws CustomerNotFoundException;
    
    public List<CustomerEntity> retrieveAllCustomers();

    public CustomerEntity retrieveCustomerOfReservationID(Long reservationID);

}
