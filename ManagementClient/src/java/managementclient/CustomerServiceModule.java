/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.CarEntity;
import entity.CustomerEntity;
import entity.EmployeeEntity;
import entity.ReservationEntity;
import java.util.List;
import java.util.Scanner;
import util.enumeration.StatusEnum;
import util.exception.InvalidReservationCodeException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author stonley
 */
public class CustomerServiceModule {
    
    private CustomerSessionBeanRemote customerSessionBeanRemote; 
    private CarSessionBeanRemote carSessionBeanRemote; 
    private ReservationSessionBeanRemote reservationSessionBeanRemote; 
    
    private EmployeeEntity currentEmployee;
    
    public CustomerServiceModule() {

    }
    
    public CustomerServiceModule(CustomerSessionBeanRemote customerSessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, EmployeeEntity currentEmployee) {
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuCustomerService() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS :: Customer Service ***\n");
            System.out.println("1: Pick Up Car");
            System.out.println("2: Return Car");
            System.out.println("3: Logout\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    
                    try {
                        doPickUpCar();
                    } catch (InvalidReservationCodeException ex) {
                        System.out.println("Invalid reservation code " + ex.getMessage());
                    }
                    
                } else if (response == 2) {
                    doReturnCar();
                } else if (response == 3) {
                    doLogout();
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }

    private void doLogout() {
        System.out.println("You have successfully logged out!\n");
        this.currentEmployee = null;
    }
    
    //retrieve car from allocate car method from ejb timer, car should have been associated by now
    private void doPickUpCar() throws InvalidReservationCodeException {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Customer Service :: Pick Up Car ***\n");
        
        System.out.print("Enter Reservation Code> ");         
        String reservationCode = sc.nextLine().trim();
        ReservationEntity reservation = null; 
        try {
            reservation = reservationSessionBeanRemote.retrieveReservationByReservationCode(reservationCode); 
        } catch (ReservationNotFoundException ex) {
            throw new InvalidReservationCodeException("Reservation code is invalid");  
        }
        
        // check if offline payment mode
        if (!reservation.isOnlinePayment()) {
            // make payment
            double paymentAmount = reservation.getRentalFee(); 
            System.out.println("Please pay: " + paymentAmount);
            
            double receivedAmount = sc.nextDouble(); 
            
            while (receivedAmount != paymentAmount) {
                System.out.println("Wrong amount, try again!");
                receivedAmount = sc.nextDouble();
            }  
        } 
        
        carSessionBeanRemote.pickUpCar(reservation);
        System.out.println("Car is picked up!");
        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doReturnCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Customer Service :: Return Car ***\n");
        
        System.out.print("Enter Reservation Code> ");         
        String reservationCode = sc.nextLine().trim();
        
        carSessionBeanRemote.returnCar(reservationCode);
        
        System.out.println("Car returned!");
        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }
    
}
