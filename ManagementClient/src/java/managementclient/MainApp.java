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
import entity.EmployeeEntity;
import java.util.Scanner;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import util.enumeration.EmployeeAccessRightEnum;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author stonley
 */
public class MainApp {
    
//    private StaffEntitySessionBeanRemote staffEntitySessionBeanRemote;
//    private ProductEntitySessionBeanRemote productEntitySessionBeanRemote;
//    private SaleTransactionEntitySessionBeanRemote saleTransactionEntitySessionBeanRemote;
//    private CheckoutSessionBeanRemote checkoutBeanRemote;
//    private EmailSessionBeanRemote emailSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private CarCategorySessionBeanRemote carCategorySessionBeanRemote;
    private CarModelSessionBeanRemote carModelSessionBeanRemote;
    private CarSessionBeanRemote carSessionBeanRemote;
    private DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote;
    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    

    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;

    private CustomerServiceModule customerServiceModule;
    private SalesManagementModule salesManagementModule;

    private EmployeeEntity currentEmployee;

    public MainApp() {
        this.currentEmployee = null;
    }
    
    public MainApp(CarCategorySessionBeanRemote carCategorySessionBeanRemote, CarModelSessionBeanRemote carModelSessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, 
            DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote) {
        this.carCategorySessionBeanRemote = carCategorySessionBeanRemote;
        this.carModelSessionBeanRemote = carModelSessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.dispatchRecordSessionBeanRemote = dispatchRecordSessionBeanRemote;
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
    }


//    public MainApp(Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory) {
//        this.staffEntitySessionBeanRemote = staffEntitySessionBeanRemote;
//        this.productEntitySessionBeanRemote = productEntitySessionBeanRemote;
//        this.saleTransactionEntitySessionBeanRemote = saleTransactionEntitySessionBeanRemote;
//        this.checkoutBeanRemote = checkoutBeanRemote;
//        this.emailSessionBeanRemote = emailSessionBeanRemote;
//
//        this.queueCheckoutNotification = queueCheckoutNotification;
//        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
//    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to CARMS Management Client ***\n");
            System.out.println("1: Employee Login");
            System.out.println("2: Exit\n");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

                        customerServiceModule = new CustomerServiceModule();
                        salesManagementModule = new SalesManagementModule(rentalRateSessionBeanRemote, carCategorySessionBeanRemote, carModelSessionBeanRemote, carSessionBeanRemote, dispatchRecordSessionBeanRemote, currentEmployee);
                        
                        menuMain();
                        
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }
    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** CaRMS Management Client :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        try {
            currentEmployee = employeeSessionBeanRemote.loginEmployee(username, password);
        } catch (InvalidLoginCredentialException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }
        
    }

    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        //while (true) {
            System.out.println("*** CaRMS Management Client ***\n"); //may not need, let the system auto recognise the employees access rights
            System.out.println("You are logged in as " + currentEmployee.getName() + " " + " with " + currentEmployee.getEmployeeAccessRight().toString() + " rights\n");
            
            if (currentEmployee.getEmployeeAccessRight() == EmployeeAccessRightEnum.SALES_MANAGER) {
                salesManagementModule.menuSalesManagementForSales();
//                    try {
//                        salesManagementModule.menuSalesManagement();
//                    } catch (InvalidAccessRightException ex) {
//                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
//                    }
            }
            
            if (currentEmployee.getEmployeeAccessRight() == EmployeeAccessRightEnum.OPERATIONS_MANAGER) {
                salesManagementModule.menuSalesManagementForOperations();
            }
            
            if (currentEmployee.getEmployeeAccessRight() == EmployeeAccessRightEnum.CUSTOMER_SERVICE_EXEC) {
                customerServiceModule.menuCustomerService();
            }
            
        //}
    }

    
}
