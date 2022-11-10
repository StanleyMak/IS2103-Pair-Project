/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.OwnCustomerEntity;
import javax.ejb.Remote;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author hanyang
 */
@Remote
public interface CustomerSessionBeanRemote {
    
    public Long createNewCustomer(CustomerEntity customer);

    public CustomerEntity retrieveCustomerByID(Long customerID);

    public OwnCustomerEntity retrieveOwnCustomerByOwnCustomerUsername(String username) throws CustomerNotFoundException;

    public void deleteCustomer(Long customerID);

    public OwnCustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException;

}
