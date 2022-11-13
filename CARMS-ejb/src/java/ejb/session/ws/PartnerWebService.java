/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RentalRateSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.CarCategoryEntity;
import entity.OutletEntity;
import entity.PartnerEntity;
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
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.CarCategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidReservationCodeException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author stonley
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @EJB
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @EJB(name = "RentalRateSessionBeanLocal")
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "partnerLogin")
    public PartnerEntity partnerLogin() throws InvalidLoginCredentialException {

        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** Holiday Reservation Client :: Partner Login ***\n");
        System.out.print("Enter Email> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            PartnerEntity partner = partnerSessionBeanLocal.partnerLogin(username, password);
            System.out.println("********** PartnerWebService.partnerLogin(): Partner " + partner.getUsername() + " login remotely via web service");
            return partner;
        } else {
            throw new InvalidLoginCredentialException("Missing Login Credentials");
        }
    }

    @WebMethod(operationName = "partnerSearchCar")
    public void partnerSearchCar() {

        try {

            Scanner sc = new Scanner(System.in);
            System.out.println("*** Holiday Reservation Client :: Parnter Search Car ***\n");

            System.out.print("Enter Start Date (DD/MM/YYYY hh:mm) (24hr format)> ");
            String startDateTime = sc.nextLine();

            Date pickupDateTime = dateTimeFormat.parse(startDateTime);
            LocalDateTime pickupDateTimeLocal = LocalDateTime.ofInstant(pickupDateTime.toInstant(), ZoneId.systemDefault());
            LocalTime pickupTime = pickupDateTimeLocal.toLocalTime();

            System.out.print("Enter Pick Up Outlet> ");
            String pickupOutletAddress = sc.nextLine().trim();
            OutletEntity pickupOutlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(pickupOutletAddress);
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
            OutletEntity returnOutlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(returnOutletAddress);
            LocalTime outletClosingHours = LocalDateTime.ofInstant(returnOutlet.getCloseHour().toInstant(), ZoneId.systemDefault()).toLocalTime();

            if (returnTime.isAfter(outletClosingHours)) {
                System.out.println("Invalid return time, outlet closes at: " + outletClosingHours + "\n");
                System.out.println("Press Enter To Continue...");
                sc.nextLine();
                return;
            }

            //return list of abled cars
            List<CarCategoryEntity> availableCat = carSessionBeanLocal.doSearchCar(pickupDateTime, returnDateTime, pickupOutlet, returnOutlet);

            System.out.println("Available Car Categories For Rental:");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-5s%-20s%-10s\n", "No.", "Category Name", "Rental Fee");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            int i = 1;
            for (CarCategoryEntity cat : availableCat) {
                double rentalFee = rentalRateSessionBeanLocal.computeCheapestRentalRateFee(pickupDateTime, returnDateTime, cat.getCategoryName());
                System.out.printf("%-5s%-20s%-10s\n", i, availableCat.get(i - 1).getCategoryName(), rentalFee);
                i++;
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.println("Press Enter To Continue...");
            sc.nextLine();

        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

    }

    @WebMethod(operationName = "partnerReserveCar")
    public void partnerReserveCar(@WebParam(name = "partner") PartnerEntity partner) {

        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** CaRMS Reservation Client :: Reserve Car ***\n");

            System.out.print("Enter Start Date (DD/MM/YYYY hh:mm) (24hr format)> ");
            String startDateTime = sc.nextLine();
            Date pickupDateTime = dateTimeFormat.parse(startDateTime);

            LocalDateTime pickupDateTimeLocal = LocalDateTime.ofInstant(pickupDateTime.toInstant(), ZoneId.systemDefault());
            LocalTime pickupTime = pickupDateTimeLocal.toLocalTime();

            System.out.print("Enter Pick Up Outlet> "); // A, B, C -> check opening/closing hours
            String pickupOutletAddress = sc.nextLine().trim();
            OutletEntity pickupOutlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(pickupOutletAddress);
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
            OutletEntity returnOutlet = outletSessionBeanLocal.retrieveOutletByOutletAddress(returnOutletAddress);
            LocalTime outletClosingHours = LocalDateTime.ofInstant(returnOutlet.getCloseHour().toInstant(), ZoneId.systemDefault()).toLocalTime();

            if (returnTime.isAfter(outletClosingHours)) {
                System.out.println("Invalid return time, outlet closes at: " + outletClosingHours);
            }

            //return list of abled cars
            List<CarCategoryEntity> availableCat = carSessionBeanLocal.doSearchCar(pickupDateTime, returnDateTime, pickupOutlet, returnOutlet);
            double rentalFee = 0;
            System.out.println("Available Car Categories For Rental:");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-5s%-20s%-10s\n", "No.", "Category Name", "Rental Fee");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            int i = 1;
            for (CarCategoryEntity cat : availableCat) {
                rentalFee = rentalRateSessionBeanLocal.computeCheapestRentalRateFee(pickupDateTime, returnDateTime, cat.getCategoryName());
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
            reservation.setReservationCode(partner.getUsername() + finalDate);
            reservation.setStartDateTime(pickupDateTime);
            reservation.setEndDateTime(returnDateTime);
            reservation.setRentalFee(rentalFee);
            reservation.setPickUpOutlet(pickupOutlet);
            reservation.setReturnOutlet(returnOutlet);

            String resCode = reservationSessionBeanLocal.createNewReservation(reservation, partner.getUsername(), returnOutletAddress, pickupOutletAddress, carCategory.getCategoryName());
            System.out.println("New Reservation " + resCode + " successfully created!\n");

            sc.nextLine();
            System.out.println("Press Enter To Continue...");
            sc.nextLine();
        } catch (ParseException | CustomerNotFoundException | CarCategoryNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        } 

    }

    @WebMethod(operationName = "partnerCancelReservation")
    public void partnerCancelReservation(@WebParam(name = "partner") PartnerEntity partner,
            @WebParam(name = "reservation") ReservationEntity res) {

        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: Cancel Reservation ***\n");
        String username = partner.getUsername();
        String reservationCode = res.getReservationCode();

        Date currDate = new Date();
        try {
            String cancelMessage = reservationSessionBeanLocal.cancelReservation(username, reservationCode, currDate); ///CHECK IF THIS IS FOR CUSTOMER OR PARTNER
            System.out.println(cancelMessage);
            System.out.println("Reservation " + reservationCode + " successfully cancelled!\n");
        } catch (ReservationNotFoundException | CustomerNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }

        System.out.println("Press Enter To Continue...");
        sc.nextLine();

    }

    @WebMethod(operationName = "partnerViewReservationDetails")
    public void partnerViewReservationDetails(@WebParam(name = "partner") PartnerEntity partner) {

        Scanner sc = new Scanner(System.in);
        System.out.println("*** CaRMS Reservation Client :: View Reservation Details ***\n");

        System.out.print("Enter Reservation Code> ");
        String reservationCode = sc.nextLine().trim();

        ReservationEntity reservation = null;
        try {
            reservation = reservationSessionBeanLocal.retrieveReservationByReservationCode(reservationCode);
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
            partnerCancelReservation(partner, reservation);
        } else {
            System.out.println("Press Enter To Continue...");
            sc.nextLine();
        }
    }

    @WebMethod(operationName = "partnerViewAllReservations")
    public void partnerViewAllReservations(@WebParam(name = "loggedInPartner") PartnerEntity loggedInPartner) {

        Scanner sc = new Scanner(System.in);
        System.out.println("*** Holiday Reservation Client :: View All My Reservations ***\n");

        PartnerEntity partner = partnerSessionBeanLocal.retrievePartnerByID(loggedInPartner.getPartnerID());
        List<ReservationEntity> reservations = reservationSessionBeanLocal.retrieveReservationsOfPartnerID(loggedInPartner.getPartnerID());

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

    @WebMethod(operationName = "partnerLogout")
    public PartnerEntity partnerLogout(@WebParam(name = "loggedInPartner") PartnerEntity loggedInPartner) {

        Scanner sc = new Scanner(System.in);
        System.out.println("*** Holiday Reservation Client :: Partner Logout ***\n");

        if (loggedInPartner != null) {
            loggedInPartner = null;
            System.out.println("You have been successfully logged out!\n");
        } else {
            System.out.println("Nobody is logged in!");
        }

        return loggedInPartner;

    }

}
