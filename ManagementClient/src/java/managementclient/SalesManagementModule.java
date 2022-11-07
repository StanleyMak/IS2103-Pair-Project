/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.CarCategorySessionBeanRemote;
import ejb.session.stateless.CarModelSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import entity.CarModelEntity;
import entity.RentalRateEntity;
import java.time.Clock;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.faces.validator.Validator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 *
 * @author stonley
 */
public class SalesManagementModule {

//    private final ValidatorFactory validatorFactory;
//    private final Validator validator;
    
    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    private CarCategorySessionBeanRemote carCategorySessionBeanRemote;
    private CarModelSessionBeanRemote carModelSessionBeanRemote;

    public SalesManagementModule() {
//        this.validatorFactory = Validation.buildDefaultValidatorFactory();
//        this.validator = (Validator) validatorFactory.getValidator();
    }

    public void menuSalesManagementForSales() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS :: Sales Management (Sales Manager) ***\n");
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
            System.out.println("*** CaRMS :: Sales Management (Operations Manager) ***\n");
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
        RentalRateEntity rentalRate = new RentalRateEntity();

        System.out.print("Enter Rental Rate Name> ");
        rentalRate.setRentalName(sc.nextLine().trim());

        // does this mean that rnetal rate points at car category??
        System.out.println("Enter Car Category> ");
        String carCategoryName = sc.nextLine().trim();

        System.out.print("Enter Rate Per Day (in Dollars)> ");
        rentalRate.setRatePerDay(sc.nextDouble());

        System.out.print("Enter Start Date (DD/MM/YYYY)> ");
        String startDateString = sc.nextLine();
        String[] startDateSplit = startDateString.split("/");
        Date startDate = new Date(Integer.parseInt(startDateSplit[2]), Integer.parseInt(startDateSplit[1]), Integer.parseInt(startDateSplit[0]));
        rentalRate.setStartDate(startDate);

        System.out.print("Enter End Date (DD/MM/YYYY)> ");
        String endDateString = sc.nextLine();
        String[] endDateSplit = endDateString.split("/");
        Date endDate = new Date(Integer.parseInt(endDateSplit[2]), Integer.parseInt(endDateSplit[1]), Integer.parseInt(endDateSplit[0]));
        rentalRate.setEndDate(endDate);

        Long rentalRateID = rentalRateSessionBeanRemote.createNewRentalRate(rentalRate);
        System.out.println("New Rental Rate: " + rentalRate.getRentalRateID() + " successfully created!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewAllRentalRates() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: View All Rental Rates ***\n");
        List<RentalRateEntity> rentalRates = rentalRateSessionBeanRemote.retrieveAllRentalRates();
        Collections.sort(rentalRates);

        for (RentalRateEntity rentalRate : rentalRates) {
            System.out.println("Rental Rate " + rentalRate.getRentalName());
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doViewRentalRateDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: View Rental Rate Details ***\n");

        System.out.print("Enter Rental Rate Name> ");
        RentalRateEntity rentalRate = rentalRateSessionBeanRemote.retrieveRentalRateByRentalRateName(sc.nextLine().trim());

        System.out.println("*** Rental Rate: " + rentalRate.getRentalName() + " ***\n");
        System.out.println("Rate Per Day: " + rentalRate.getRatePerDay());
        System.out.println("Start Date: " + rentalRate.getStartDate());
        System.out.println("End Date: " + rentalRate.getEndDate());

        Integer response = 0;

        while (true) {
            System.out.println("1: Update Rental Rate");
            System.out.println("2: Delete Rental Rate");
            System.out.println("3: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    doUpdateRentalRate(rentalRate);
                } else if (response == 2) {
                    doDeleteRentalRate(rentalRate);
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

//        System.out.println("Press Enter To Continue...");
//        sc.nextLine();
//        
    }

    private void doUpdateRentalRate(RentalRateEntity rentalRate) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: Update Rental Rate ***\n");
        Integer response = 0;

        // does this mean that rnetal rate points at car category??
        while (true) {
            System.out.println("Update Car Category?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    System.out.print("Enter Car Category> ");
                    String carCategoryName = sc.nextLine().trim();
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

        while (true) {
            System.out.println("Update Rate Per Day (in Dollars)?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    System.out.print("Enter Rate Per Day (in Dollars)> ");
                    rentalRate.setRatePerDay(sc.nextDouble());
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

        while (true) {
            System.out.println("Update Start Date (DD/MM/YYYY)?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    System.out.print("Enter Start Date (DD/MM/YYYY)> ");
                    String startDateString = sc.nextLine();
                    String[] startDateSplit = startDateString.split("/");
                    Date startDate = new Date(Integer.parseInt(startDateSplit[2]), Integer.parseInt(startDateSplit[1]), Integer.parseInt(startDateSplit[0]));
                    rentalRate.setStartDate(startDate);
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

        while (true) {
            System.out.println("Update End Date (DD/MM/YYYY)?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    System.out.print("Enter End Date (DD/MM/YYYY)> ");
                    String endDateString = sc.nextLine();
                    String[] endDateSplit = endDateString.split("/");
                    Date endDate = new Date(Integer.parseInt(endDateSplit[2]), Integer.parseInt(endDateSplit[1]), Integer.parseInt(endDateSplit[0]));
                    rentalRate.setEndDate(endDate);
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

        rentalRateSessionBeanRemote.updateRentalRate(rentalRate);
        System.out.println("Rental Rate: " + rentalRate.getRentalRateID() + " successfully updated!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doDeleteRentalRate(RentalRateEntity rentalRate) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: Delete Rental Rate ***\n");
        
        rentalRateSessionBeanRemote.deleteRentalRate(rentalRate.getRentalRateID());

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doCreateNewModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Create New Model ***\n");
        CarModelEntity carModel = new CarModelEntity();
        
        System.out.println("Enter Model Name> ");
        carModel.setModelName(sc.nextLine().trim());
        
        System.out.println("Enter Car Category> ");
        String carCategoryName = sc.nextLine().trim();
        
        Long carModelID = carModelSessionBeanRemote.createNewCarModel(carModel, carCategoryName);
        System.out.println("New Car Model: " + carModelID + " successfully created!\n");

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
