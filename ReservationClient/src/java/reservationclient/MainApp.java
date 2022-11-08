/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.CustomerEntity;
import entity.EmployeeEntity;
import entity.OutletEntity;
import entity.ReservationEntity;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author stonley
 */
public class MainApp {
    
     private CustomerSessionBeanRemote customerSessionBeanRemote; 
     private ReservationSessionBeanRemote reservationSessionBeanRemote; 
     private CarSessionBeanRemote carSessionBeanRemote; 
     private CustomerEntity loggedInCustomer; 
     
    
    public MainApp() {
        this.loggedInCustomer = null;
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, CustomerEntity loggedInCustomer) {
        this(); 
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.loggedInCustomer = loggedInCustomer;
    }
    
    

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to CARMS Resevration Client ***\n");
            System.out.println("1: Customer Login");
            System.out.println("2: Register As Customer");
            System.out.println("3: Search Car");
            System.out.println("4: Exit\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    doLogin();

//                    try {
//                        doLogin();
//                        System.out.println("Login successful!\n");
//                        menuMain();
//                    } catch (InvalidLoginCredentialException ex) {
//                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
//                    }
                } else if (response == 2) {
                    doRegisterAsCustomer();
                } else if (response == 3) {
                    doSearchCarForVisitor();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    public void menuMain() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS Reservation Client ***\n");
            System.out.println("1: Search Car");
            System.out.println("2: Reserve Car");
            System.out.println("--------------------------");
            System.out.println("3: Cancel Reservation");
            System.out.println("4: View All My Reservation");
            System.out.println("--------------------------");
            System.out.println("5: Customer Logout");
            response = 0;

            while (response < 1 || response > 7) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    doSearchCarForCustomer();
                } else if (response == 2) {
                    doReserveCar();
                } else if (response == 3) {
                    doViewReservationDetails();
                } else if (response == 4) {
                    doViewAllMyReservations();
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 5) {
                break;
            }
        }
    }

    public void doRegisterAsCustomer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client ::  ***\n");
        CustomerEntity newCustomer = new CustomerEntity();
        System.out.print("Enter email> ");
        newCustomer.setEmail(scanner.nextLine().trim());
        
        System.out.print("Enter desired password> ");
        newCustomer.setPassword(scanner.nextLine().trim());
        
        Long customerID = customerSessionBeanRemote.createNewCustomer(newCustomer);
          
        
        System.out.println("You have successfully registered as a customer!");
        
        System.out.println("Press Enter To Continue...");
        scanner.nextLine();
    }

    public void doSearchCarForVisitor() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client ::  ***\n");
        
        System.out.println("Enter pick up date/time");
        String pickUpDate = sc.nextLine().trim(); 
        System.out.println("Enter pick up outlet");
        String pickUpOutlet = sc.nextLine().trim(); 
        
        System.out.println("Enter return date/time");
        String returnDate = sc.nextLine().trim(); 
        System.out.println("Enter return outlet");
        String returnOutlet = sc.nextLine().trim(); 
        /*
        int idx = 0;
        List<CarEntity> availableCars = carSessionBeanRemote.retrieveCarsByDate(pickUpDate, pickUpOutlet, returnDate, returnOutlet); 
        idx++; 
        
        for (CarEntity car : availableCars) {
            System.out.println(String.format("%s. %s %s", idx, car.getModel(), car.getModel().getCategory()));
        }
        */
        List<ReservationEntity> reservations = reservationSessionBeanRemote.retrieveAllReservations();
        
        // check if car is available via reservations
        
        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }
    
    
    public void doSearchCarForCustomer() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client ::  ***\n");
        
        System.out.println("Enter pick up date/time");
        String pickUpDate = sc.nextLine().trim(); 
        System.out.println("Enter pick up outlet");
        String pickUpOutlet = sc.nextLine().trim(); 
        
        System.out.println("Enter return date/time");
        String returnDate = sc.nextLine().trim(); 
        System.out.println("Enter return outlet");
        String returnOutlet = sc.nextLine().trim(); 
        /*
        int idx = 0;
        List<CarEntity> availableCars = carSessionBeanRemote.retrieveCarsByDate(pickUpDate, pickUpOutlet, returnDate, returnOutlet); 
        idx++; 
        for (CarEntity car : availableCars) {
            System.out.println(String.format("%s. %s %s", idx, car.getModel(), car.getModel().getCategory()));
        }
        */
        
        System.out.println("Do you want to reserve a car in the list? (yes/no)");
        String response = sc.nextLine().trim(); 
        
        if (response.equals("yes")) {
            doReserveCar(); 
        } else {
            // bye
        }
        
        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }
    
    
    private void doLogin() { //throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** CaRMS Reservation Client :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if (username.length() > 0 && password.length() > 0) {
            customerSessionBeanRemote.customerLogin(username, password); 
        } else {
            // throw new InvalidLoginCredentialException("Missing login credentials");
        }
    }
    
    private void doReserveCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Reserve Car ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doCancelReservation(ReservationEntity reservationEntity) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Cancel Reservation ***\n");
        
        reservationSessionBeanRemote.deleteReservation(reservationEntity.getReservationID());
        System.out.println("Reservation cancelled");
        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doViewReservationDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View Reservation Details ***\n");
        
        System.out.println("Enter your reservation code");
        String reservationCode = sc.nextLine().trim(); 
        
        ReservationEntity reservation = reservationSessionBeanRemote.retrieveReservationByReservationCode(reservationCode); 
        
        // print details
        System.out.println(reservation.getReservationCode());
        System.out.println(reservation.getDuration());
        System.out.println(reservation.getRentalFee());
        System.out.println(reservation.getStartDateTime());
        System.out.println(reservation.getEndDateTime());
        System.out.println(reservation.getPickUpOutlet());
        System.out.println(reservation.getReturnOutlet());
        
        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doViewAllMyReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View All My Reservations ***\n");
        
        List<ReservationEntity> reservations = loggedInCustomer.getReservations(); 
        
        for (ReservationEntity res : reservations) {
            System.out.println(res);
        }
         
        System.out.println("Press Enter To Continue...");
        sc.nextLine();   
    }
    
    private void doLogout() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Logout ***\n");
        
        if (loggedInCustomer != null) {
            loggedInCustomer = null; 
            System.out.println("You have been successfully logged out!\n");
        } else {
            System.out.println("Nobody is logged in!");
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }

    
}
