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
import ejb.session.stateless.RentalRateSessionBeanRemote;
import entity.CarEntity;
import entity.CarModelEntity;
import entity.DispatchRecordEntity;
import entity.EmployeeEntity;
import entity.RentalRateEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.faces.validator.Validator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import util.enumeration.RentalRateTypeEnum;
import util.enumeration.StatusEnum;
import util.exception.CarCategoryNotFoundException;
import util.exception.CarModelNameExistsException;
import util.exception.CarModelNotFoundException;
import util.exception.DeleteCarModelException;

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
    private CarSessionBeanRemote carSessionBeanRemote;
    private DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote;

    private EmployeeEntity currentEmployee;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public SalesManagementModule() {
//        this.validatorFactory = Validation.buildDefaultValidatorFactory();
//        this.validator = (Validator) validatorFactory.getValidator();
    }

    public SalesManagementModule(RentalRateSessionBeanRemote rentalRateSessionBeanRemote, CarCategorySessionBeanRemote carCategorySessionBeanRemote, CarModelSessionBeanRemote carModelSessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, DispatchRecordSessionBeanRemote dispatchRecordSessionBeanRemote, EmployeeEntity currentEmployee) {
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.carCategorySessionBeanRemote = carCategorySessionBeanRemote;
        this.carModelSessionBeanRemote = carModelSessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.dispatchRecordSessionBeanRemote = dispatchRecordSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    //

    public void menuSalesManagementForSales() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS :: Sales Management (Sales Manager) ***\n");
            System.out.println("1: Create Rental Rate");
            System.out.println("2: View All Rental Rates");
            System.out.println("3: View Rental Details");
            System.out.println("4: Logout\n");
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
                    doLogout();
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
            System.out.println("11: Logout\n");
            response = 0;

            while (response < 1 || response > 11) {
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
                    doLogout();
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 11) {
                return;
            }
        }
    }

    private void doLogout() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Management Client :: Logout ***\n");
        if (currentEmployee != null) {
            currentEmployee = null;
            System.out.println("You have been successfully logged out!\n");
        } else {
            System.out.println("Nobody is logged in!");
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doCreateRentalRate() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: Create Rental Rate ***\n");
        RentalRateEntity rentalRate = new RentalRateEntity();

        System.out.print("Enter Rental Rate Name> ");
        rentalRate.setRentalName(sc.nextLine().trim());

        // does this mean that rnetal rate points at car category??
        System.out.print("Enter Car Category> ");
        String carCategoryName = sc.nextLine().trim();

        System.out.println("Enter Rental Rate Type> ");
        Integer response = 0;

        while (true) {
            System.out.println("1: Default");
            System.out.println("2: Promo");
            System.out.println("3: Peak");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response > 0 && response < 4) {
                    rentalRate.setRentalRateType(RentalRateTypeEnum.values()[response - 1]);
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response > 0 && response < 4) {
                break;
            }
        }

        System.out.print("Enter Rate Per Day (in Dollars)> ");
        rentalRate.setRatePerDay(sc.nextDouble());

        sc.nextLine();

        try {
            System.out.print("Enter Start Date (DD/MM/YYYY)> ");
            String startDate = sc.nextLine();
            if (!startDate.equals("null")) {
                rentalRate.setStartDate(dateFormat.parse(startDate));
            } else {
                rentalRate.setStartDate(null);
            }

            System.out.print("Enter End Date (DD/MM/YYYY)> ");
            String endDate = sc.nextLine();
            if (!endDate.equals("null")) {
                rentalRate.setEndDate(dateFormat.parse(endDate));
            } else {
                rentalRate.setEndDate(null);
            }

            try {
                Long rentalRateID = rentalRateSessionBeanRemote.createNewRentalRate(rentalRate, carCategoryName); //add category or associate category here
                System.out.println("New Rental Rate: " + rentalRateID + " successfully created!\n");
            } catch (CarCategoryNotFoundException e) {
                System.out.println("Error: " + e.getMessage() + "!\n");
            }

        } catch (ParseException e) {
            System.out.println("Invalid Date/Time Format! Please Try Again!\n");
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewAllRentalRates() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: View All Rental Rates ***\n");
        List<RentalRateEntity> rentalRates = rentalRateSessionBeanRemote.retrieveAllRentalRates();

        System.out.println("Rental Rate Records:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-5s%-40s%-18s%-20s%-20s%-28s%-28s\n", "No.", "ID", "Name", "Type", "Car Category", "Rate per Day ($)", "Validity Period (Start)", "Validity Period (End)");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (RentalRateEntity rentalRate : rentalRates) {
            if (rentalRate.getStartDate() != null && rentalRate.getEndDate() != null) {
                System.out.printf("%-5s%-5s%-40s%-18s%-20s%-20s%-28s%-28s\n", i, rentalRate.getRentalRateID(), rentalRate.getRentalName(), rentalRate.getRentalRateType().toString(), rentalRate.getCarCategory().getCategoryName(), rentalRate.getRatePerDay(), dateFormat.format(rentalRate.getStartDate()), dateFormat.format(rentalRate.getEndDate()));
            } else {
                System.out.printf("%-5s%-5s%-40s%-18s%-20s%-20s%-28s%-28s\n", i, rentalRate.getRentalRateID(), rentalRate.getRentalName(), rentalRate.getRentalRateType().toString(), rentalRate.getCarCategory().getCategoryName(), rentalRate.getRatePerDay(), "null", "null");
            }
            i++;
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doViewRentalRateDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: View Rental Rate Details ***\n");

        System.out.print("Enter Rental Rate Name> ");
        RentalRateEntity rentalRate = rentalRateSessionBeanRemote.retrieveRentalRateByRentalRateName(sc.nextLine().trim());

        System.out.println("Rental Rate Record:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-40s%-18s%-20s%-20s%-28s%-28s\n", "ID", "Name", "Type", "Car Category", "Rate per Day ($)", "Validity Period (Start)", "Validity Period (End)");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        if (rentalRate.getStartDate() != null && rentalRate.getEndDate() != null) {
            System.out.printf("%-5s%-40s%-18s%-20s%-20s%-28s%-28s\n", rentalRate.getRentalRateID(), rentalRate.getRentalName(), rentalRate.getRentalRateType().toString(), rentalRate.getCarCategory().getCategoryName(), rentalRate.getRatePerDay(), dateFormat.format(rentalRate.getStartDate()), dateFormat.format(rentalRate.getEndDate()));
        } else {
            System.out.printf("%-5s%-40s%-18s%-20s%-20s%-28s%-28s\n", rentalRate.getRentalRateID(), rentalRate.getRentalName(), rentalRate.getRentalRateType().toString(), rentalRate.getCarCategory().getCategoryName(), rentalRate.getRatePerDay(), "null", "null");
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();

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
                    break;
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2 || response == 3) {
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

        while (true) {
            System.out.println("Update Rental Rate Name?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter Rental Rate Name> ");
                    rentalRate.setRentalName(sc.nextLine().trim());
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
            System.out.println("Update Rental Rate Type?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.println("Enter Rental Rate Type> ");
                    Integer res = 0;

                    while (true) {
                        System.out.println("1: Default");
                        System.out.println("2: Promo");
                        System.out.println("3: Peak");
                        res = 0;

                        while (res < 1 || res > 3) {
                            System.out.print("> ");

                            res = sc.nextInt();

                            if (res > 0 && res < 4) {
                                rentalRate.setRentalRateType(RentalRateTypeEnum.values()[res - 1]);
                                break;
                            } else {
                                System.out.println("Invalid option, please try again!\n");
                            }
                        }
                        if (res > 0 && res < 4) {
                            break;
                        }
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

        String carCategoryName = "";
        while (true) {
            System.out.println("Update Car Category?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter Car Category> ");
                    carCategoryName = sc.nextLine().trim();
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
                sc.nextLine();

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
            System.out.println("Update Start Date?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter Start Date (DD/MM/YYYY)> ");
                    String startDate = sc.nextLine().trim();

                    try {
                        if (!startDate.equals("null")) {
                            rentalRate.setStartDate(dateFormat.parse(startDate));
                        } else {
                            rentalRate.setStartDate(null);
                        }
                    } catch (ParseException e) {
                        System.out.println("Invalid Date Format! Please try again!\n");
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

        while (true) {
            System.out.println("Update End Date?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter End Date (DD/MM/YYYY)> ");
                    String endDate = sc.nextLine().trim();

                    try {
                        if (!endDate.equals("null")) {
                            rentalRate.setEndDate(dateFormat.parse(endDate));
                        } else {
                            rentalRate.setEndDate(null);
                        }
                    } catch (ParseException e) {
                        System.out.println("Invalid Date Format! Please try again!\n");
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

        try {
            if (carCategoryName.equals("")) {
                rentalRateSessionBeanRemote.updateRentalRate(rentalRate, rentalRate.getCarCategory().getCategoryName());
            } else {
                rentalRateSessionBeanRemote.updateRentalRate(rentalRate, carCategoryName);
            }
        } catch (CarCategoryNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

        System.out.println("Rental Rate: " + rentalRate.getRentalRateID() + " successfully updated!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doDeleteRentalRate(RentalRateEntity rentalRate) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Sales) :: Delete Rental Rate ***\n");

        String rentalRateName = rentalRate.getRentalName();

        rentalRateSessionBeanRemote.deleteRentalRate(rentalRate.getRentalRateID());

        System.out.println("Rental Rate: " + rentalRateName + " successfully deleted!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doCreateNewModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Create New Model ***\n");
        CarModelEntity carModel = new CarModelEntity();

        System.out.print("Enter Model Make> ");
        carModel.setModelMake(sc.nextLine().trim());

        System.out.print("Enter Model Name> ");
        carModel.setModelName(sc.nextLine().trim());

        System.out.print("Enter Car Category> ");
        String carCategoryName = sc.nextLine().trim();

        try {
            Long carModelID = carModelSessionBeanRemote.createNewCarModel(carModel, carCategoryName);
            System.out.println("New Car Model: " + carModelID + " successfully created!\n");
        } catch (CarModelNameExistsException | CarCategoryNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewAllModels() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View All Models ***\n");

        List<CarModelEntity> carModels = carModelSessionBeanRemote.retrieveAllCarModels();

        System.out.println("Car Model Records:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-5s%-40s%-15s%-15s\n", "No.", "ID", "Car Category", "Make", "Model");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (CarModelEntity carModel : carModels) {
            System.out.printf("%-5s%-5s%-40s%-15s%-15s\n", i, carModel.getCarModelID(), carModel.getCategory().getCategoryName(), carModel.getModelMake(), carModel.getModelName());
            i++;
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doUpdateModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Update Model ***\n");
        Integer response = 0;

        System.out.print("Enter Car Model Name> ");
        String carModelName = sc.nextLine();
        CarModelEntity carModel = null;
        try {
            carModel = carModelSessionBeanRemote.retrieveCarModelByCarModelName(carModelName);
        } catch (CarModelNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }
        String carCategoryName = "";
        while (true) {
            System.out.println("Update Car Category?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter New Car Category> ");
                    carCategoryName = sc.nextLine().trim();
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
            System.out.println("Update Car Model Make?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter New Car Model Make> ");
                    carModel.setModelMake(sc.nextLine().trim());
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
            System.out.println("Update Car Model Name?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter New Car Model Name> ");
                    carModel.setModelName(sc.nextLine().trim());
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

        try {
            if (carCategoryName.equals("")) {
                carModelSessionBeanRemote.updateCarModel(carModel, carModel.getCategory().getCategoryName());
            } else {
                carModelSessionBeanRemote.updateCarModel(carModel, carCategoryName);
            }
        } catch (CarModelNotFoundException | CarCategoryNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        } 

        System.out.println("Car Model: " + carModel.getModelName() + " successfully updated!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doDeleteModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Delete Model ***\n");

        System.out.print("Enter Car Model Name> ");
        String carModelName = sc.nextLine();

        try {
            carModelSessionBeanRemote.deleteCarModel(carModelName);
        } catch (CarModelNotFoundException | DeleteCarModelException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        } 
        
        System.out.println("Car Model: " + carModelName + " successfully deleted!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doCreateNewCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Create New Car ***\n");

        CarEntity car = new CarEntity();

        System.out.print("Enter Car License Plate Number> ");
        car.setLicensePlateNumber(sc.nextLine().trim());

        System.out.print("Enter Car Colour> ");
        car.setColour(sc.nextLine().trim());

        System.out.print("Enter Car Model Name> ");
        String carModelName = sc.nextLine().trim();

        System.out.print("Enter Outlet Address> ");
        String outletAddress = sc.nextLine().trim();

        car.setStatus(StatusEnum.AVAILABLE);

        try {
            Long carID = carSessionBeanRemote.createNewCar(car, carModelName, outletAddress);
            System.out.println("New Car: " + carID + " succcessfully created!\n");
        } catch (CarModelNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewAllCars() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View All Cars ***\n");

        //license colour model status outlet
        List<CarEntity> cars = carSessionBeanRemote.retrieveAllCars();

        System.out.println("Car Records:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-40s%-15s%-30s%-15s%-15s%-15s\n", "ID", "License Plate Number", "Colour", "Category", "Make", "Model", "Status"/*, "Outlet"*/);
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (CarEntity car : cars) {
            System.out.printf("%-5s%-40s%-15s%-30s%-15s%-15s%-15s\n", car.getCarID(), car.getLicensePlateNumber(), car.getColour(), car.getModel().getCategory().getCategoryName(), car.getModel().getModelMake(), car.getModel().getModelName(), car.getStatus().toString()/*, car.getOutlet().getOutletAddress()*/);
            i++;
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewCarDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View Car Details ***\n");

        System.out.print("Enter Car License Plate Number> ");
        CarEntity car = carSessionBeanRemote.retrieveCarByCarLicensePlateNumber(sc.nextLine().trim());

        System.out.println("Car Record:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-40s%-15s%-30s%-15s%-15s%-15s\n", "ID", "License Plate Number", "Colour", "Category", "Make", "Model", "Status"/*, "Outlet"*/);
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-40s%-15s%-30s%-15s%-15s%-15s\n", car.getCarID(), car.getLicensePlateNumber(), car.getColour(), car.getModel().getCategory().getCategoryName(), car.getModel().getModelMake(), car.getModel().getModelName(), car.getStatus().toString()/*, car.getOutlet().getOutletAddress()*/);
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();

        Integer response = 0;
        while (true) {
            System.out.println("1: Update Car");
            System.out.println("2: Delete Car");
            System.out.println("3: Back\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    doUpdateCar(car);
                } else if (response == 2) {
                    doDeleteCar(car);
                    break;
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if (response == 2 || response == 3) {
                break;
            }
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doUpdateCar(CarEntity car) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Update Car ***\n");

        Integer response = 0;
        while (true) {
            System.out.println("Update License Plate Number?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter New License Plate Number> ");
                    car.setLicensePlateNumber(sc.nextLine().trim());
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
            System.out.println("Update Colour?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter New Colour> ");
                    car.setColour(sc.nextLine().trim());
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

        String carModelName = "";
        while (true) {
            System.out.println("Update Car Model?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter New Car Model Name> ");
                    carModelName = sc.nextLine().trim();
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

        String outletAddress = "";
        while (true) {
            System.out.println("Update Outlet?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    System.out.print("Enter New Outlet Address> ");
                    outletAddress = sc.nextLine().trim();
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
            System.out.println("Update Status?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();
                sc.nextLine();

                if (response == 1) {
                    doUpdateCarStatus(car);
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

        if (carModelName.equals("")) {
            carModelName = car.getModel().getModelName();
        }
        if (outletAddress.equals("")) {
            outletAddress = car.getCurrOutlet().getAddress();
        }

        try {
            carSessionBeanRemote.updateCar(car, carModelName, outletAddress);
        } catch (CarModelNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }
        System.out.println("Car: " + car.getLicensePlateNumber() + " successfully updated!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private void doUpdateCarStatus(CarEntity car) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter New Status> ");
        Integer response = 0;
        while (true) {
            System.out.println("1: Available");
            System.out.println("2: On Rental");
            System.out.println("3: In Transit");
            System.out.println("4: Repair");
            System.out.println("5: Disabled");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response > 0 && response < 6) {
                    car.setStatus(StatusEnum.values()[response - 1]);
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if (response > 0 && response < 6) {
                break;
            }
        }
    }

    private void doDeleteCar(CarEntity car) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Delete Car ***\n");

        carSessionBeanRemote.deleteCar(car.getCarID());
        System.out.println("Car: " + car.getLicensePlateNumber() + " successfully deleted!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewDisptachRecordsForCurrentDayReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View Transit Dispatch Records For Current Day Reservations ***\n");

        System.out.print("Enter Today's Date> ");
        Date today = new Date(sc.nextLine());

//        List<DispatchRecordEntity> dispatchRecords = dispatchRecordSessionBeanRemote.retrieveDispatchRecordsForCurrentDayCurrentOutlet(today, currentEmployee.getOutlet());
//        
//        for (DispatchRecordEntity dispatchRecord : dispatchRecords) {
//            System.out.println("Transit Driver Dispatch Record: " + dispatchRecord.getDispatchRecordID());
//        }
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

        System.out.print("Enter Dispatch Record ID> ");
        Long dispatchRecordID = sc.nextLong();

        //dispatchRecordSessionBeanRemote.updateDispatchRecordAsCompleted(dispatchRecordID);
        System.out.println("Dispatch Record: " + dispatchRecordID + " successfully updated as complete!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

}
