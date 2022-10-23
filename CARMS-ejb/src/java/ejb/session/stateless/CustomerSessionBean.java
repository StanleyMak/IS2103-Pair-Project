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


/**
 *
 * @author hanyang
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    public Long createNewCustomer(CustomerEntity customer) {
        em.persist(customer); 
        em.flush();
        
        return customer.getCustomerID(); 
    }
    
    public CustomerEntity retrieveCustomerByID(Long customerID) {
        return em.find(CustomerEntity.class, customerID);
    }
   
}
