/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsreservationclient;

import java.util.Scanner;

/**
 *
 * @author stonley
 */
public class MainApp {

    public MainApp() {

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
                    doSearchCar();
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
                    doSearchCar();
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
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client ::  ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    public void doSearchCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client ::  ***\n");

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
    }
    
    private void doReserveCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Reserve Car ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doCancelReservation() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Cancel Reservation ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doViewReservationDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View Reservation Details ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doViewAllMyReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View All My Reservations ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doLogout() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Logout ***\n");

        System.out.println("You have been successfully logged out!\n");
        
        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }

}
