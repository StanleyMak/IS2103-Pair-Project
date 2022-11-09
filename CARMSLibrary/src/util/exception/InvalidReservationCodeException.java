/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author hanyang
 */
public class InvalidReservationCodeException extends Exception {

    /**
     * Creates a new instance of <code>InvalidReservationCodeException</code>
     * without detail message.
     */
    public InvalidReservationCodeException() {
    }

    /**
     * Constructs an instance of <code>InvalidReservationCodeException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidReservationCodeException(String msg) {
        super(msg);
    }
}
