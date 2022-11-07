/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import entity.OutletEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author stonley
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewEmployee(EmployeeEntity employee, OutletEntity outlet) {
        //employee.setOutlet(outlet);
        em.persist(employee);
        em.flush();
        return employee.getEmployeeID();
    }
    
    @Override
    public EmployeeEntity retrieveEmployeeByEmployeeID(Long employeeID) {
        EmployeeEntity employee = em.find(EmployeeEntity.class, employeeID);
        return employee;
    }
    
    @Override
    public EmployeeEntity retrieveEmployeeByEmployeeEmail(String email) {
        Query query = em.createQuery("SELECT e FROM EmployeeEntity WHERE e.email = ?1")
                .setParameter(1, email);
        EmployeeEntity employee = (EmployeeEntity) query.getSingleResult();
        return employee;
    }
    
    @Override
    public EmployeeEntity loginEmployee(String email, String password) { //throws InvalidLoginCredentialException {
        EmployeeEntity employee = retrieveEmployeeByEmployeeEmail(email);
        return employee;
//        if (employee.getPassword().equals(password)) {
//            return employee;
//        } else {
//            throw new InvalidLoginCredentialException("Email or Password Incorrect");
//        }
    }

    
}
