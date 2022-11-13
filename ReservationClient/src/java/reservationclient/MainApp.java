/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.CarModelEntity;
import entity.CustomerEntity;
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
import javax.persistence.PersistenceException;
import util.exception.CarCategoryNotFoundException;
import util.exception.CustomerEmailExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidReservationCodeException;
import util.exception.ReservationCodeExistsException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

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
    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    private OwnCustomerEntity loggedInCustomer;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public MainApp() {
        this.loggedInCustomer = null;
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, OutletSessionBeanRemote outletSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.outletSessionBeanRemote = outletSessionBeanRemote;
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
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
            System.out.println("1: Search Car");
            System.out.println("2: Reserve Car");
            System.out.println("----------------------------------");
            System.out.println("3: View Reservation Details");
            System.out.println("4: View All My Reservation");
            System.out.println("----------------------------------");
            System.out.println("5: Customer Logout");
            response = 0;

            while (response < 1 || response > 5) {
                System.out.print("> ");

                response = sc.nextInt();

                if (response == 1) {
                    try {
                        doSearchCarForVisitor();
                    } catch (ParseException e) {
                        System.out.println("Invalid Date/Time Format!\n");
                    }
                } else if (response == 2) {
                    List<CarEntity> cars = new ArrayList<>();
                    doReserveCar(cars);
                } else if (response == 3) {
                    doViewReservationDetails();
                } else if (response == 4) {
                    doViewAllMyReservations();
                } else if (response == 5) {
                    doLogout();
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
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** CaRMS Reservation Client :: Register As Customer ***\n");
            OwnCustomerEntity newCustomer = new OwnCustomerEntity();
            
            System.out.print("Enter Email> ");
            newCustomer.setEmail(scanner.nextLine().trim());
            
            System.out.print("Enter Password> ");
            newCustomer.setPassword(scanner.nextLine().trim());
            
            Long customerID = customerSessionBeanRemote.createNewCustomer(newCustomer);
            
            System.out.println("New Customer: " + customerID + " successfully registered!\n");
            
            System.out.println("Press Enter To Continue...");
            scanner.nextLine();
        } catch (CustomerEmailExistsException | UnknownPersistenceException | InputDataValidationException | PersistenceException ex) {
            System.out.println("Error: " + ex.getMessage() + "!\n");
        } 
        }
    }

    //CATCH EXCEPTION NOT THROW
    public void doSearchCarForVisitor() throws java.text.ParseException {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Search Car ***\n");

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
            System.out.println("Invalid Pick Up Time! Outlet opens at: " + outletOpeningHours + "\n");
            System.out.println("Press Enter To Continue...");
            sc.nextLine();
            return;
        }

        System.out.print("Enter End Date (DD/MM/YYYY hh:mm (24hr format))> ");
        String endDateTime = sc.nextLine();
        Date returnDateTime = dateTimeFormat.parse(endDateTime);
        LocalDateTime returnDateTimeLocal = LocalDateTime.ofInstant(returnDateTime.toInstant(), ZoneId.systemDefault());
        LocalTime returnTime = returnDateTimeLocal.toLocalTime();

        System.out.print("Enter Return Outlet Address> ");
        String returnOutletAddress = sc.nextLine().trim();
        OutletEntity returnOutlet = outletSessionBeanRemote.retrieveOutletByOutletAddress(returnOutletAddress);
        LocalTime outletClosingHours = LocalDateTime.ofInstant(returnOutlet.getCloseHour().toInstant(), ZoneId.systemDefault()).toLocalTime();

        if (returnTime.isAfter(outletClosingHours)) {
            System.out.println("Invalid return time, outlet closes at: " + outletClosingHours + "\n");
            System.out.println("Press Enter To Continue...");
            sc.nextLine();
            return;
        }

        //return list of abled cars
        List<CarCategoryEntity> availableCat = carSessionBeanRemote.doSearchCar(pickupDateTime, returnDateTime, pickupOutlet, returnOutlet);

        System.out.println("Available Car Categories For Rental:");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-20s%-10s\n", "No.", "Category Name", "Rental Fee");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (CarCategoryEntity cat : availableCat) {
            double rentalFee = rentalRateSessionBeanRemote.computeCheapestRentalRateFee(pickupDateTime, returnDateTime, cat.getCategoryName());
            System.out.printf("%-5s%-20s%-10s\n", i, availableCat.get(i - 1).getCategoryName(), rentalFee);
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

        try {
            
            Scanner sc = new Scanner(System.in);
            System.out.println("*** CaRMS Reservation Client :: Reserve Car ***\n");
            
            System.out.print("Enter Start Date (DD/MM/YYYY hh:mm) (24hr format)> ");
            String startDateTime = sc.nextLine();
            // converting input time to HH:mm format
            Date pickupDateTime = null;
            try {
                pickupDateTime = dateTimeFormat.parse(startDateTime);
            } catch (ParseException e) {
                System.out.println("Invalid Date/Time Format");
            }
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
            Date returnDateTime = null;
            try {
                returnDateTime = dateTimeFormat.parse(endDateTime);
            } catch (ParseException e) {
                System.out.println("Invalid Date/Time Format");
            }
            
            LocalDateTime returnDateTimeLocal = LocalDateTime.ofInstant(returnDateTime.toInstant(), ZoneId.systemDefault());
            LocalTime returnTime = returnDateTimeLocal.toLocalTime();
            
            System.out.print("Enter Return Outlet Address> ");
            String returnOutletAddress = sc.nextLine().trim();
            OutletEntity returnOutlet = outletSessionBeanRemote.retrieveOutletByOutletAddress(returnOutletAddress);
            LocalTime outletClosingHours = LocalDateTime.ofInstant(returnOutlet.getCloseHour().toInstant(), ZoneId.systemDefault()).toLocalTime();
            
            if (returnTime.isAfter(outletClosingHours)) {
                System.out.println("Invalid return time, outlet closes at: " + outletClosingHours);
            }
            
            //return list of abled cars
            List<CarCategoryEntity> availableCat = carSessionBeanRemote.doSearchCar(pickupDateTime, returnDateTime, pickupOutlet, returnOutlet);
            double rentalFee = 0;
            System.out.println("Available Car Categories For Rental:");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-5s%-20s%-10s\n", "No.", "Category Name", "Rental Fee");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            int i = 1;
            for (CarCategoryEntity cat : availableCat) {
                rentalFee = rentalRateSessionBeanRemote.computeCheapestRentalRateFee(pickupDateTime, returnDateTime, cat.getCategoryName());
                System.out.printf("%-5s%-20s%-10s\n", i, availableCat.get(i - 1).getCategoryName(), rentalFee);
                i++;
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            
            //RESEVRE STARTS HERE -----------------------------------------------------
            CarCategoryEntity carCategory = null;
            Integer response = 0;
            
            while (true) {
                System.out.println("Select A Category Number: ");
                response = 0;
                
                while (response < 1 || response > availableCat.size()) {
                    System.out.print("> ");
                    
                    response = sc.nextInt();
                    
                    if (response > availableCat.size()) {
                        System.out.println("Invalid option, please try again!\n");
                    } else {
                        carCategory = availableCat.get(response - 1);
                        break;
                    }
                }
                
                if (response > 0 || response < availableCat.size() + 1) {
                    break;
                }
            }
            sc.nextLine();
            ReservationEntity reservation = new ReservationEntity();
            System.out.print("Enter Credit Card Number> ");
            reservation.setCreditCardNumber(sc.nextLine().trim());
            
            Integer res = 0;
            while (true) {
                System.out.println("Payment Choice:");
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
            String[] dateString = new Date().toString().split(" ");
            String finalDate = "";
            for (int j = 0; j < dateString.length; j++) {
                finalDate += dateString[j];
            }
            reservation.setReservationCode(this.loggedInCustomer.getEmail() + finalDate);
            reservation.setStartDateTime(pickupDateTime);
            reservation.setEndDateTime(returnDateTime);
            reservation.setRentalFee(rentalFee);
            reservation.setPickUpOutlet(pickupOutlet);
            reservation.setReturnOutlet(returnOutlet);
            
            
            String resCode = reservationSessionBeanRemote.createNewReservation(reservation, this.loggedInCustomer.getEmail(), returnOutletAddress, pickupOutletAddress, carCategory.getCategoryName());
            System.out.println("New Reservation " + resCode + " successfully created!\n");    
            sc.nextLine();
            System.out.println("Press Enter To Continue...");
            sc.nextLine();
        } catch (CustomerNotFoundException | ReservationCodeExistsException | InputDataValidationException | UnknownPersistenceException | CarCategoryNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "!\n");
        } 
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

    private void doCancelReservation(CustomerEntity customer, ReservationEntity res) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Cancel Reservation ***\n");
        String email = customer.getEmail();
        String reservationCode = res.getReservationCode();

        Date currDate = new Date();
        try {
            String cancelMessage = reservationSessionBeanRemote.cancelReservation(email, reservationCode, currDate);
            System.out.println(cancelMessage);
            System.out.println("Reservation " + reservationCode + " successfully cancelled!\n");
        } catch (ReservationNotFoundException | CustomerNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    private void doViewReservationDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View Reservation Details ***\n");

        System.out.print("Enter Reservation Code> ");
        String reservationCode = sc.nextLine().trim();

        ReservationEntity reservation = null;
        try {
            reservation = reservationSessionBeanRemote.retrieveReservationByReservationCode(reservationCode);
        } catch (ReservationNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "!\n");
        }

        System.out.println("Reservation Record:");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-50s%-20s%-28s%-28s%-20s%-20s%-5s\n", "ID", "Code", "Rental Fee ($)", "Start Date/Time", "End Date/Time", "Pick Up Outlet", "Return Outlet", "Online Payment?");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        String onlinePayment = "";
        if (reservation.isOnlinePayment()) {
            onlinePayment = "YES";
        } else {
            onlinePayment = "NO";
        }
        System.out.printf("%-5s%-50s%-20s%-28s%-28s%-20s%-20s%-5s\n", reservation.getReservationID(), reservation.getReservationCode(), reservation.getRentalFee(), dateTimeFormat.format(reservation.getStartDateTime()), dateTimeFormat.format(reservation.getEndDateTime()), reservation.getPickUpOutlet().getAddress(), reservation.getReturnOutlet().getAddress(), onlinePayment);
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();

        System.out.print("Cancel Reservation? (Y/N)> ");
        String cancel = sc.nextLine().trim();

        if (cancel.equalsIgnoreCase("Y")) {
            doCancelReservation(this.loggedInCustomer, reservation);
        } else {
            System.out.println("Press Enter To Continue...");
            sc.nextLine();
        }

    }

    private void doViewAllMyReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View All My Reservations ***\n");
        
        CustomerEntity customer = null;
        
        try {
            customer = customerSessionBeanRemote.retrieveCustomerByID(this.loggedInCustomer.getCustomerID());
        } catch (CustomerNotFoundException ex) {
            System.out.println("Error retrieving reservations");
        }
        
        List<ReservationEntity> reservations = customer.getReservations();

        System.out.println("Reservation Records:");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s%-5s%-50s%-20s%-28s%-28s%-20s%-20s%-5s\n", "No.", "ID", "Code", "Rental Fee ($)", "Start Date/Time", "End Date/Time", "Pick Up Outlet", "Return Outlet", "Online Payment?");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (ReservationEntity reservation : reservations) {
            String onlinePayment = "";
            if (reservation.isOnlinePayment()) {
                onlinePayment = "YES";
            } else {
                onlinePayment = "NO";
            }
            System.out.printf("%-5s%-5s%-50s%-20s%-28s%-28s%-20s%-20s%-5s\n", i, reservation.getReservationID(), reservation.getReservationCode(), reservation.getRentalFee(), dateTimeFormat.format(reservation.getStartDateTime()), dateTimeFormat.format(reservation.getEndDateTime()), reservation.getPickUpOutlet().getAddress(), reservation.getReturnOutlet().getAddress(), onlinePayment);
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
