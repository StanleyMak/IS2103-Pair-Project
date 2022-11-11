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
import entity.CarEntity;
import entity.OutletEntity;
import entity.OwnCustomerEntity;
import entity.ReservationEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
                        System.out.println("Parse error");
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
                    } catch (ParseException ex) {
                        System.out.println("parse error");
                    }
                } else if (response == 2) {
                    doReserveCar();
                } else if (response == 3) {
                    try {
                        doViewReservationDetails();
                    } catch (InvalidReservationCodeException ex) {
                        System.out.println("Invalid reservation details: " + ex.getMessage());
                    }
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client ::  ***\n");
        OwnCustomerEntity newCustomer = new OwnCustomerEntity();

        System.out.print("Enter Email> ");
        newCustomer.setEmail(scanner.nextLine().trim());

        System.out.print("Enter Password> ");
        newCustomer.setPassword(scanner.nextLine().trim());

        Long customerID = customerSessionBeanRemote.createNewCustomer(newCustomer);

        System.out.println("New Customer: " + customerID + " successfully registered as a customer!\n");

        System.out.println("Press Enter To Continue...");
        scanner.nextLine();
    }

    public void doSearchCarForVisitor() throws java.text.ParseException {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Search Car (Visitor) ***\n");

        System.out.print("Enter Start Date (DD/MM/YYYY Hours:Minutes (24hr format)> ");
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
            System.out.println("Invalid pick up time, outlet opens at: " + outletOpeningHours);
        }

        System.out.print("Enter End Date (DD/MM/YYYY HH:mm (24hr format))> ");
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

        List<CarEntity> availableCars = carSessionBeanRemote.doSearchCar(pickupDateTime, returnDateTime, pickupOutlet, returnOutlet);

        for (CarEntity car : availableCars) {
            System.out.println(car.getModel().getCategory().getCategoryName() + "| " + car.getModel().getModelName() + "| " + car.getModel().getModelMake() + "| ");
//            System.out.printf("%-5s%-28s%-28s%-20s%-20s%-5s\n", car.getModel().getCategory().getCategoryName(), car.getModel().getModelName(), reservation.getRentalFee(), car.getModel().getModelMake())
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
    }

    public void doSearchCarForCustomer() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Search Car (Customer) ***\n");

        System.out.println("Enter pick up date/time");
        String pickUpDate = sc.nextLine().trim();
        System.out.println("Enter pick up outlet");
        String pickUpOutlet = sc.nextLine().trim();

        System.out.println("Enter return date/time");
        String returnDate = sc.nextLine().trim();
        System.out.println("Enter return outlet");
        String returnOutlet = sc.nextLine().trim();

        System.out.println("Do you want to reserve a car in the list? (yes/no)");
        String response = sc.nextLine().trim();

        if (response.equals("yes")) {
            doReserveCar();
        } else {
            // bye
        }

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

    private void doReserveCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Reserve Car ***\n");

        System.out.println("which car do you want to reserve?>");
        int response = sc.nextInt();
        sc.nextLine();

        // loop through list of cars and find the one matching to response = carID
        ReservationEntity reservation = new ReservationEntity();

        // setters based on doSearchCar parameters
        // reservationSessionBeanRemote.createNewReservation(reservation, Long.MIN_VALUE, email, returnOutletAddress, pickupOutletAddress);
        System.out.println("Press Enter To Continue...");
        sc.nextLine();

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
            System.out.println("Error occured while trying to cancel reservation");
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

        System.out.println("Press Enter To Continue...");
        sc.nextLine();
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
