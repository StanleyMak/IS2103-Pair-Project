/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.OwnCustomerEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author hanyang
 */
@Local
public interface CustomerSessionBeanLocal {

    public Long createNewCustomer(CustomerEntity customer);

    public CustomerEntity retrieveCustomerByID(Long customerID);

    public void deleteCustomer(Long customerID);

    public OwnCustomerEntity customerLogin(String email, String password) throws InvalidLoginCredentialException;

    public CustomerEntity retrieveCustomerByCustomerEmail(String email) throws CustomerNotFoundException;

    public OwnCustomerEntity retrieveOwnCustomerByOwnCustomerEmail(String email) throws CustomerNotFoundException;

    public List<CustomerEntity> retrieveAllCustomers();

    public CustomerEntity retrieveCustomerOfReservationID(Long reservationID);
    
}
