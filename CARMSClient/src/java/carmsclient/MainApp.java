/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsclient;

import java.util.Scanner;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

/**
 *
 * @author stonley
 */
public class MainApp {

    private StaffEntitySessionBeanRemote staffEntitySessionBeanRemote;
    private ProductEntitySessionBeanRemote productEntitySessionBeanRemote;
    private SaleTransactionEntitySessionBeanRemote saleTransactionEntitySessionBeanRemote;
    private CheckoutSessionBeanRemote checkoutBeanRemote;
    private EmailSessionBeanRemote emailSessionBeanRemote;
    
    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;
    
    private CashierOperationModule cashierOperationModule;
    private SystemAdministrationModule systemAdministrationModule;
    
    private StaffEntity currentStaffEntity;
    
    
    
    public MainApp() 
    {        
    }

    
    
    public MainApp(Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory)
    {
        this.staffEntitySessionBeanRemote = staffEntitySessionBeanRemote;
        this.productEntitySessionBeanRemote = productEntitySessionBeanRemote;
        this.saleTransactionEntitySessionBeanRemote = saleTransactionEntitySessionBeanRemote;
        this.checkoutBeanRemote = checkoutBeanRemote;
        this.emailSessionBeanRemote = emailSessionBeanRemote;
        
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }
    
    
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to CARMS Management Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        customerServiceModule = new customerServiceModule();
                        salesManagementModule = new salesManagementModule();
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** CaRMS Management Client :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentStaffEntity = staffEntitySessionBeanRemote.staffLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    
    
    private void menuMain()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Point-of-Sale (POS) System (v4.2) ***\n");
            System.out.println("You are login as " + currentStaffEntity.getFirstName() + " " + currentStaffEntity.getLastName() + " with " + currentStaffEntity.getAccessRightEnum().toString() + " rights\n");
            System.out.println("1: Cashier Operation");
            System.out.println("2: System Administration");
            System.out.println("3: Logout\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    cashierOperationModule.menuCashierOperation();
                }
                else if(response == 2)
                {
                    try
                    {
                        systemAdministrationModule.menuSystemAdministration();
                    }
                    catch (InvalidAccessRightException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3)
            {
                break;
            }
        }
    }
}

