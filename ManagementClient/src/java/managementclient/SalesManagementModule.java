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
import java.time.Clock;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.faces.validator.Validator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import util.enumeration.StatusEnum;

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
        System.out.println("You have successfully logged out!\n");
        currentEmployee = null;
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

        System.out.print("Enter Rate Per Day (in Dollars)> ");
        rentalRate.setRatePerDay(sc.nextDouble());
        
        sc.nextLine();

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

        Long rentalRateID = rentalRateSessionBeanRemote.createNewRentalRate(rentalRate); //add category or associate category here
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

        System.out.print("Enter Model Name> ");
        carModel.setModelName(sc.nextLine().trim());

        System.out.print("Enter Car Category> ");
        String carCategoryName = sc.nextLine().trim();

        Long carModelID = carModelSessionBeanRemote.createNewCarModel(carModel, carCategoryName);
        System.out.println("New Car Model: " + carModelID + " successfully created!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewAllModels() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View All Models ***\n");

        List<CarModelEntity> carModels = carModelSessionBeanRemote.retrieveAllCarModels();

        //sort list
        for (CarModelEntity carModel : carModels) {
            System.out.println("Car Model: " + carModel.getModelName());
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doUpdateModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Update Model ***\n");
        Integer response = 0;

        System.out.println("Enter Car Model Name> ");
        String carModelName = sc.nextLine();
        CarModelEntity carModel = carModelSessionBeanRemote.retrieveCarModelByCarModelName(carModelName);

        while (true) {
            System.out.println("Update Car Model Name?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = sc.nextInt();

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

        carModelSessionBeanRemote.updateCarModel(carModel);

        System.out.println("Car Model: " + carModel.getModelName() + " successfully updated!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doDeleteModel() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: Delete Model ***\n");

        System.out.print("Enter Car Model Name> ");
        String carModelName = sc.nextLine();

        carModelSessionBeanRemote.deleteCarModel(carModelName);

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

        car.setStatus(StatusEnum.AVAILABLE);

        Long carID = carSessionBeanRemote.createNewCar(car);
        System.out.println("New Car: " + car.getCarID() + " succcessfully created!\n");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewAllCars() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View All Cars ***\n");

        List<CarEntity> cars = carSessionBeanRemote.retrieveAllCars();

        //sort
        for (CarEntity car : cars) {
            System.out.println("Car License Plate Number: " + car.getLicensePlateNumber());
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewCarDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS :: Sales Management (Operations) :: View Car Details ***\n");

        System.out.print("Enter Car License Plate Number> ");
        CarEntity car = carSessionBeanRemote.retrieveCarByCarLicensePlateNumber(sc.nextLine().trim());

        System.out.println("Car License Plate Number: " + car.getLicensePlateNumber());
        System.out.println("Car Colour: " + car.getColour());
        System.out.println("Car Status: " + car.getStatus());

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

            while (true) {
                System.out.println("Update Colour?");
                System.out.println("1: Yes");
                System.out.println("2: No");
                response = 0;

                while (response < 1 || response > 2) {
                    System.out.print("> ");

                    response = sc.nextInt();

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

                while (true) {
                    System.out.println("Update Status?");
                    System.out.println("1: Yes");
                    System.out.println("2: No");
                    response = 0;

                    while (response < 1 || response > 2) {
                        System.out.print("> ");

                        response = sc.nextInt();

                        if (response == 1) {
                            System.out.print("Enter New Status> ");
                            Integer res = 0;
                            while (true) {
                                System.out.println("1: Available");
                                System.out.println("2: On Rental");
                                System.out.println("3: In Transit");
                                System.out.println("4: Servicing");
                                System.out.println("5: Disabled");
                                System.out.println("6: Back\n");
                                response = 0;

                                while (response < 1 || response > 6) {
                                    System.out.print("> ");

                                    res = sc.nextInt();

                                    if (res != 6) {
                                        car.setStatus(StatusEnum.values()[res]);
                                    } else if (res == 6) {
                                        break;
                                    } else {
                                        System.out.println("Invalid option, please try again!\n");
                                    }
                                }
                                if (res == 6) {
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
            }
            
            carSessionBeanRemote.updateCar(car);
            System.out.println("Car: " + car.getLicensePlateNumber() + " successfully updated!\n");
            
            System.out.println("Press Enter To Continue...");
            sc.nextLine();

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
