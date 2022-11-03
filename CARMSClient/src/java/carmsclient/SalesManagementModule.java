/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsclient;

import java.util.Scanner;
import javax.faces.validator.Validator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 *
 * @author stonley
 */
public class SalesManagementModule {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public SalesManagementModule() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = (Validator) validatorFactory.getValidator();
    }

    public void menuSalesManagement() { //may not need, let the system auto recognise the employee access rights
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS :: Sales Management ***\n");
            System.out.println("Select Role\n");
            System.out.println("1: Sales Manager");
            System.out.println("2: Operations Manager");
            System.out.println("3: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    menuSalesManagementForSales();
                } else if (response == 2) {
                    menuSalesManagementForOperations();
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

    public void menuSalesManagementForSales() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS :: Sales Management (Sales) ***\n");
            System.out.println("1: Create Rental Rate");
            System.out.println("2: View All Rental Rates");
            System.out.println("3: View Rental Details");
            System.out.println("4: Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    doCreateRentalRate();
                } else if (response == 2) {
                    doViewAllRentalRates();
                } else if (response == 3) {
                    doViewRentalRateDetails();
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

    public void menuSalesManagementForOperations() {

//        if(currentStaffEntity.getAccessRightEnum() != AccessRightEnum.MANAGER)
//        {
//            throw new InvalidAccessRightException("You don't have MANAGER rights to access the system administration module.");
//        }
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS :: Sales Management (Operations) ***\n");
            System.out.println("1: Create New Model");
            System.out.println("2: View All Models");
            System.out.println("3: Update Model");
            System.out.println("4: Delete Model");
            System.out.println("-------------------------------------------------------------");
            System.out.println("5: Create New Car");
            System.out.println("6: View All Cars");
            System.out.println("7: View Car Details");
            System.out.println("-------------------------------------------------------------");
            System.out.println("8: View Transit Dispatch Records For Current Day Reservations");
            System.out.println("9: Assign Transit Driver");
            System.out.println("10: Update Transit As Completed");
            System.out.println("-------------------------------------------------------------");
            System.out.println("11: Back\n");
            response = 0;

            while (response < 1 || response > 7) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    doCreateNewModel();
                } else if (response == 2) {
                    doViewAllModels();
                } else if (response == 3) {
                    doUpdateModel();
                } else if (response == 4) {
                    doDeleteModel();
                } else if (response == 5) {
                    doCreateNewCar();
                } else if (response == 6) {
                    doViewAllCars();
                } else if (response == 7) {
                    doViewCarDetails();
                } else if (response == 8) {
                    doViewDisptachRecordsForCurrentDayReservations();
                } else if (response == 9) {
                    doAssignTransitDriver();
                } else if (response == 10) {
                    doUpdateTransitAsCompleted();
                } else if (response == 11) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 11) {
                break;
            }
        }
    }
    
    private void doCreateRentalRate() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: Create Rental Rate ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }
    
    private void doViewAllRentalRates() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: View All Rental Rates ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doViewRentalRateDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: View Rental Rate Details ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }

    private void doUpdateRentalRate() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: Update Rental Rate ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doDeleteRentalRate() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: Delete Rental Rate ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
        
    }
    
    private void doCreateNewModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Create New Model ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewAllModels() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View All Models ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doUpdateModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Update Model ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doDeleteModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Delete Model ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doCreateNewCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Create New Car ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewAllCars() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View All Cars ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewCarDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View Car Details ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doUpdateCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Update Car ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doDeleteCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Delete Car ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewDisptachRecordsForCurrentDayReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View Transit Dispatch Records For Current Day Reservations ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doAssignTransitDriver() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Assign Transit Driver ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doUpdateTransitAsCompleted() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Update Transit As Completed ***\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

}
