/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.OwnCustomerEntity;
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
        //customer.getXX.size();
        return customer;
    }
    
    @Override
    public OwnCustomerEntity retrieveOwnCustomerByOwnCustomerUsername(String username) throws CustomerNotFoundException {
        
        Query query = em.createQuery("SELECT c FROM OwnCustomerEntity c WHERE c.username LIKE ?1")
                .setParameter(1, username);
        
        //customer.getXX.size();
        // association
        
        try {
            OwnCustomerEntity customer = (OwnCustomerEntity) query.getSingleResult(); 
            return customer; 
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer username " + username + "does not exist!");
        }
    }
    
    @Override
    public void deleteCustomer(Long customerID) {
        CustomerEntity customer = retrieveCustomerByID(customerID);
        //dissociate
        em.remove(customer);
    }
    
    @Override
    public OwnCustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException { 
        try {
            OwnCustomerEntity customer = retrieveOwnCustomerByOwnCustomerUsername(username);
            
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
    
    
}
