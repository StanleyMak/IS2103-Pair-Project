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
import entity.ReservationEntity;
import java.util.List;
import java.util.Scanner;
import util.enumeration.StatusEnum;

/**
 *
 * @author stonley
 */
public class CustomerServiceModule {
    
    private CustomerSessionBeanRemote customerSessionBeanRemote; 
    private CarSessionBeanRemote carSessionBeanRemote; 
    ReservationSessionBeanRemote reservationSessionBeanRemote; 
    
    public CustomerServiceModule() {

    }

    public void menuCustomerService() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS :: Customer Service ***\n");
            System.out.println("1: Pick Up Car");
            System.out.println("2: Return Car");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    doPickUpCar();
                } else if (response == 2) {
                    doReturnCar();
                } else if (response == 3) {
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

    private void doPickUpCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Customer Service :: Pick Up Car ***\n");
        
        System.out.println("Enter customer email");
        String email = sc.nextLine().trim(); 
        System.out.println("Enter reservation code");         
        String reservationCode = sc.nextLine().trim();
        
        ReservationEntity reservation = reservationSessionBeanRemote.retrieveReservationByReservationCode(reservationCode);
        // check if offline payment mode
        if (!reservation.isOnlinePayment()) {
            // make payment
            double paymentAmount = reservation.getRentalFee(); 
            System.out.println("Please pay: " + paymentAmount);
            
            double receivedAmount = sc.nextDouble(); 
            
            while (receivedAmount != paymentAmount) {
                System.out.println("Wrong amount");
                receivedAmount = sc.nextDouble();
            }  
        } 
        
        carSessionBeanRemote.pickUpCar(email, reservationCode);
        System.out.println("Car is picked up");
        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doReturnCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Customer Service :: Return Car ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }
    
}
