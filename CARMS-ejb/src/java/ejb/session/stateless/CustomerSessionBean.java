/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
        //customer.getXX.size();
        return customer;
    }
    
    @Override
    public CustomerEntity retrieveCustomerByCustomerUsername(String username) {
        
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.username LIKE ?1")
                .setParameter(1, username);
        CustomerEntity customer = (CustomerEntity) query; 
        //customer.getXX.size();
        return customer;
    }
    
    @Override
    public void deleteCustomer(Long customerID) {
        CustomerEntity customer = retrieveCustomerByID(customerID);
        //dissociate
        em.remove(customer);
    }
    
    @Override
    public CustomerEntity customerLogin(String username, String password) /*throws InvalidLoginCredentialException*/ { 
        
        CustomerEntity customer = retrieveCustomerByCustomerUsername(username);

        return customer;
           
    }
    
    /*
    @Override
    public CustomerEntity customerLogin(String email, String password) throws InvalidLoginCredentialException { 
        try {
            CustomerEntity customer = retrieveCustomerByCustomerUsername(email);
            
            if (password.equals(customer.getPassword())) {
                // association
                return customer; 
            } else {
                throw new InvalidLoginCredentialException("Invalid email or password"); 
            }
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid email or password");
        }   
    }
    
    */
  
    
}
