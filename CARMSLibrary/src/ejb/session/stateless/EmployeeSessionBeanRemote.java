/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import entity.OutletEntity;
import javax.ejb.Remote;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author stonley
 */
@Remote
public interface EmployeeSessionBeanRemote {
    
    public Long createNewEmployee(EmployeeEntity employee, OutletEntity outlet);

    public EmployeeEntity retrieveEmployeeByEmployeeID(Long employeeID);

    public EmployeeEntity retrieveEmployeeByEmployeeEmail(String email);

    public EmployeeEntity loginEmployee(String email, String password) throws InvalidLoginCredentialException;
    
}
