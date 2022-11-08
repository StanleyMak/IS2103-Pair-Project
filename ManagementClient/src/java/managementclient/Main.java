/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.CarCategorySessionBeanRemote;
import ejb.session.stateless.CarModelSessionBeanRemote;
import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.DispatchRecordSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author stonley
 */
public class Main {

    @EJB(name = "EmployeeSessionBeanRemote")
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;

    @EJB(name = "RentalRateSessionBeanRemote")
    private static RentalRateSessionBeanRemote rentalRateSessionBeanRemote;

    @EJB(name = "DispatchRecordSessionBeanRemote")
    private static DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote;

    @EJB(name = "CarSessionBeanRemote")
    private static CarSessionBeanRemote carSessionBeanRemote;

    @EJB(name = "CarModelSessionBeanRemote")
    private static CarModelSessionBeanRemote carModelSessionBeanRemote;

    @EJB(name = "CarCategorySessionBeanRemote")
    private static CarCategorySessionBeanRemote carCategorySessionBeanRemote;
    
    

   public static void main(String[] args) {
        MainApp mainApp = new MainApp(carCategorySessionBeanRemote, carModelSessionBeanRemote, carSessionBeanRemote, dispatchRecordSessionBeanRemote, rentalRateSessionBeanRemote, employeeSessionBeanRemote);
        mainApp.runApp();
    }
    
}
