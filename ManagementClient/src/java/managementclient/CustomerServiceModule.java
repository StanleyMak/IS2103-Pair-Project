/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import java.util.Scanner;

/**
 *
 * @author stonley
 */
public class CustomerServiceModule {
    
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
