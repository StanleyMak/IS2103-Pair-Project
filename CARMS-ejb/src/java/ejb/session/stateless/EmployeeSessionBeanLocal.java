/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import entity.OutletEntity;
import javax.ejb.Local;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author stonley
 */
@Local
public interface EmployeeSessionBeanLocal {

    public Long createNewEmployee(EmployeeEntity employee, OutletEntity outlet);

    public EmployeeEntity retrieveEmployeeByEmployeeID(Long employeeID);

    public EmployeeEntity retrieveEmployeeByEmployeeUsername(String email);

    public EmployeeEntity loginEmployee(String email, String password) throws InvalidLoginCredentialException;
    
}
