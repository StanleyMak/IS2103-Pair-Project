/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.CarModelEntity;
import entity.OutletEntity;
import entity.OwnCustomerEntity;
import entity.ReservationEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidReservationCodeException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author stonley
 */
public class MainApp {

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private CarSessionBeanRemote carSessionBeanRemote;
    // new
    private OutletSessionBeanRemote outletSessionBeanRemote;
    private OwnCustomerEntity loggedInCustomer;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public MainApp() {
        this.loggedInCustomer = null;
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, OutletSessionBeanRemote outletSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.outletSessionBeanRemote = outletSessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to CARMS Reservation Client ***\n");
            System.out.println("1: Customer Login");
            System.out.println("2: Register As Customer");
            System.out.println("3: Search Car");
            System.out.println("4: Exit\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        menuMain();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doRegisterAsCustomer();
                } else if (response == 3) {

                    try {
                        doSearchCarForVisitor();
                    } catch (ParseException ex) {
                        System.out.println("Invalid Date/Time Format! Please try again!\n");
                    }
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
            System.out.println("1: Reserve Car");
            System.out.println("----------------------------------");
            System.out.println("2: View Reservation Details");
            System.out.println("3: View All My Reservation");
            System.out.println("----------------------------------");
            System.out.println("4: Customer Logout");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    List<CarEntity> cars = new ArrayList<>();
                    doReserveCar(cars);
                } else if (response == 2) {
                    try {
                        doViewReservationDetails();
                    } catch (InvalidReservationCodeException ex) {
                        System.out.println("Invalid reservation details: " + ex.getMessage());
                    }
                } else if (response == 3) {
                    doViewAllMyReservations();
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

    public void doRegisterAsCustomer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client ::  ***\n");
        OwnCustomerEntity newCustomer = new OwnCustomerEntity();

        System.out.print("Enter Email> ");
        newCustomer.setEmail(scanner.nextLine().trim());

        System.out.print("Enter Password> ");
        newCustomer.setPassword(scanner.nextLine().trim());

        Long customerID = customerSessionBeanRemote.createNewCustomer(newCustomer);

        System.out.println("New Customer: " + customerID + " successfully registered!\n");

        System.out.println("Press Enter To Continue...");
        scanner.nextLine();
    }

    public void doSearchCarForVisitor() throws java.text.ParseException {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Search Car (Visitor) ***\n");

        System.out.print("Enter Start Date (DD/MM/YYYY hh:mm) (24hr format)> ");
        String startDateTime = sc.nextLine();
        // converting input time to HH:mm format
        Date pickupDateTime = dateTimeFormat.parse(startDateTime);
        LocalDateTime pickupDateTimeLocal = LocalDateTime.ofInstant(pickupDateTime.toInstant(), ZoneId.systemDefault());
        LocalTime pickupTime = pickupDateTimeLocal.toLocalTime();

        System.out.print("Enter Pick Up Outlet> "); // A, B, C -> check opening/closing hours
        String pickupOutletAddress = sc.nextLine().trim();
        OutletEntity pickupOutlet = outletSessionBeanRemote.retrieveOutletByOutletAddress(pickupOutletAddress);
        LocalTime outletOpeningHours = LocalDateTime.ofInstant(pickupOutlet.getOpenHour().toInstant(), ZoneId.systemDefault()).toLocalTime();

        if (pickupTime.isBefore(outletOpeningHours)) {
            System.out.println("Invalid Pick Up Time! Outlet opens at: " + outletOpeningHours);
        }

        System.out.print("Enter End Date (DD/MM/YYYY hh:mm (24hr format))> ");
        String endDateTime = sc.nextLine();
        Date returnDateTime = dateTimeFormat.parse(endDateTime);
        LocalDateTime returnDateTimeLocal = LocalDateTime.ofInstant(returnDateTime.toInstant(), ZoneId.systemDefault());
        LocalTime returnTime = returnDateTimeLocal.toLocalTime();

        System.out.print("Enter Return Outlet Address> ");
        String returnOutletAddress = sc.nextLine().trim();
        OutletEntity returnOutlet = outletSessionBeanRemote.retrieveOutletByOutletAddress(returnOutletAddress);
        LocalTime outletClosingHours = LocalDateTime.ofInstant(returnOutlet.getOpenHour().toInstant(), ZoneId.systemDefault()).toLocalTime();

        if (outletClosingHours.isBefore(outletClosingHours)) {
            System.out.println("Invalid return time, outlet closes at: " + outletClosingHours);
        }
        //SESSION BEAN CALL FOR LIST OF CATEGORIES TO BE DISPLAYED
        //retrieveAllCategories
        //for each category C,
        //retrieveReservationsOfCategory
        //for each res,
        //check if intersect with customers specified timing
        //resCount++
        
        //for cars in availableCars
        //if car.getModel().getCategory().getName() == C
        //count++
        
        //if resCount < count, display category with rental fee
        
        //RESERVE CAR
        //new res() associate everything relevant
        //associate CATEGORY 

        //return list of abled cars
        List<CarEntity> availableCars = carSessionBeanRemote.doSearchCar(pickupDateTime, returnDateTime, pickupOutlet, returnOutlet);

        System.out.println("Car Records:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-5s%-40s%-15s%-30s%-15s%-15s%-15s%-15s\n", "No.", "ID", "License Plate Number", "Colour", "Category", "Make", "Model", "Status", "Outlet"/*, Disabled*/);
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (CarEntity car : availableCars) {
            System.out.printf("%-5s%-5s%-40s%-15s%-30s%-15s%-15s%-15s%-15s\n", i, car.getCarID(), car.getLicensePlateNumber(), car.getColour(), car.getModel().getCategory().getCategoryName(), car.getModel().getModelMake(), car.getModel().getModelName(), car.getStatus().toString(), car.getCurrOutlet().getAddress());
            i++;
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";

        System.out.println("*** CaRMS Reservation Client :: Login ***\n");
        System.out.print("Enter Email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();

        if (email.length() > 0 && password.length() > 0) {
            OwnCustomerEntity customer = customerSessionBeanRemote.customerLogin(email, password);
            this.loggedInCustomer = customer;
        } else {
            throw new InvalidLoginCredentialException("Missing Login Credentials");
        }
    }

    private void doReserveCar(List<CarEntity> cars) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Reserve Car? (Y/N)> ");
        String reserve = sc.nextLine().trim();

        CarCategoryEntity carCategory = null;
        CarModelEntity carModel = null;
        Integer response = 0;

        while (true) {
            System.out.println("*** CaRMS Reservation Client :: Reserve Car ***\n");
            System.out.println("Filter By: ");
            System.out.println("1: Car Category");
            System.out.println("2: Car Make and Model");
            System.out.println("3: Exit\n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    carCategory = selectCarCategory(cars);
                    break;
                } else if (response == 2) {
                    carModel = selectCarModel(cars);
                    break;
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response > 0 && response < 4) {
                break;
            }
        }

        List<CarEntity> filteredCars = new ArrayList<>();
        if (carCategory != null) {
            filteredCars = carSessionBeanRemote.retrieveCarsFilteredByCarCategory(carCategory.getCategoryName());
        } else {
            filteredCars = carSessionBeanRemote.retrieveCarsFilteredByCarMakeAndModel(carModel.getModelMake(), carModel.getModelName());
        }

        System.out.println("Car Records:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-40s%-15s%-30s%-15s%-15s%-15s%-15s\n", "ID", "License Plate Number", "Colour", "Category", "Make", "Model", "Status", "Outlet"/*, "Rental Fee"*/);
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (CarEntity car : filteredCars) {
            System.out.printf("%-5s%-40s%-15s%-30s%-15s%-15s%-15s%-15s\n", car.getCarID(), car.getLicensePlateNumber(), car.getColour(), car.getModel().getCategory().getCategoryName(), car.getModel().getModelMake(), car.getModel().getModelName(), car.getStatus().toString(), car.getCurrOutlet().getAddress()/*, car.getRentalFee()*/);
            i++;
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.print("Select Car to Reserve> ");
        int selectedCar = sc.nextInt();
        sc.nextLine();

        // loop through list of cars and find the one matching to response = carID
        ReservationEntity reservation = new ReservationEntity();

        System.out.print("Enter Credit Card Number> ");
        reservation.setCreditCardNumber(sc.nextLine().trim());

        Integer res = 0;
        while (true) {
            System.out.print("Payment Choice:");
            System.out.println("1: Pay Now Online");
            System.out.println("2: Pay Later Upfront");
            res = 0;

            while (res < 1 || res > 2) {
                System.out.print("> ");

                res = sc.nextInt();

                if (res == 1) {
                    reservation.setOnlinePayment(true);
                    break;
                } else if (res == 2) {
                    reservation.setOnlinePayment(false);
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (res == 1 || res == 2) {
                break;
            }
        }
        
//        reservation.setStartDateTime(startDateTime);
//        reservation.setEndDateTime(endDateTime);
//        reservation.setRentalFee(rentalFee);
//        reservation.setPickUpOutlet(pickUpOutlet);
//        reservation.setReturnOutlet(returnOutlet);
//        reservation.setCar(car);

        // setters based on doSearchCar parameters
        // reservationSessionBeanRemote.createNewReservation(reservation, Long.MIN_VALUE, email, returnOutletAddress, pickupOutletAddress);
        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    private CarCategoryEntity selectCarCategory(List<CarEntity> cars) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Select Car Category: ");
        int i = 1;
        for (CarEntity car : cars) {
            System.out.println(i + ". " + car.getModel().getCategory().getCategoryName());
        }
        int response = sc.nextInt();
        return cars.get(i - 1).getModel().getCategory();
    }

    private CarModelEntity selectCarModel(List<CarEntity> cars) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Select Car Make and Model>");
        int i = 1;
        for (CarEntity car : cars) {
            System.out.println(i + ". " + car.getModel().getModelMake() + " | " + car.getModel().getModelName());
        }
        int response = sc.nextInt();
        return cars.get(i - 1).getModel();
    }

    private void doCancelReservation() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Cancel Reservation ***\n");
        System.out.print("Enter Email> ");
        String email = sc.nextLine().trim();
        System.out.print("Enter Reservation Code> ");
        String reservationCode = sc.nextLine().trim();

        try {
            reservationSessionBeanRemote.deleteReservation(email, reservationCode);
            System.out.println("Reservation " + reservationCode + " successfully cancelled!\n");
        } catch (CustomerNotFoundException | ReservationNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "!\n");
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewReservationDetails() throws InvalidReservationCodeException {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View Reservation Details ***\n");

        System.out.print("Enter Reservation Code> ");
        ReservationEntity reservation = new ReservationEntity();
        String reservationCode = sc.nextLine().trim();
        try {
            reservation = reservationSessionBeanRemote.retrieveReservationByReservationCode(reservationCode);
        } catch (ReservationNotFoundException ex) {
            throw new InvalidReservationCodeException("Reservation Code " + reservationCode + " is invalid");
        }

        System.out.println("Reservation Record:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-10s%-20s%-28s%-28s%-20s%-20s%-5s\n", "ID", "Code", "Rental Fee ($)", "Start Date/Time", "End Date/Time", "Pick Up Outlet", "Return Outlet", "Online Payment?");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        String onlinePayment = "";
        if (reservation.isOnlinePayment()) {
            onlinePayment = "YES";
        } else {
            onlinePayment = "NO";
        }
        System.out.printf("%-5s%-10s%-20s%-28s%-28s%-20s%-20s%-5s\n", reservation.getReservationID(), reservation.getReservationCode(), reservation.getRentalFee(), dateTimeFormat.format(reservation.getStartDateTime()), dateTimeFormat.format(reservation.getEndDateTime()), reservation.getPickUpOutlet().getAddress(), reservation.getReturnOutlet().getAddress(), onlinePayment);
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();

        System.out.print("Cancel Reservation? (Y/N)> ");
        String cancel = sc.nextLine().trim();

        if (cancel.equalsIgnoreCase("Y")) {
            doCancelReservation();
        } else {
            System.out.println("Press Enter To Continue...");
            sc.nextLine();
        }

    }

    private void doViewAllMyReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View All My Reservations ***\n");

        List<ReservationEntity> reservations = loggedInCustomer.getReservations();

        System.out.println("Reservation Records:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-5s%-10s%-20s%-28s%-28s%-20s%-20s%-5s\n", "No.", "ID", "Code", "Rental Fee ($)", "Start Date/Time", "End Date/Time", "Pick Up Outlet", "Return Outlet", "Online Payment?");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (ReservationEntity reservation : reservations) {
            String onlinePayment = "";
            if (reservation.isOnlinePayment()) {
                onlinePayment = "YES";
            } else {
                onlinePayment = "NO";
            }
            System.out.printf("%-5s%-5s%-10s%-20s%-28s%-28s%-20s%-20s%-5s\n", i, reservation.getReservationID(), reservation.getReservationCode(), reservation.getRentalFee(), dateTimeFormat.format(reservation.getStartDateTime()), dateTimeFormat.format(reservation.getEndDateTime()), reservation.getPickUpOutlet().getAddress(), reservation.getReturnOutlet().getAddress(), onlinePayment);
            i++;
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

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
