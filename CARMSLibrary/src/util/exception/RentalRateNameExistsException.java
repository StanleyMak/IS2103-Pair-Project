/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author stonley
 */
public class RentalRateNameExistsException extends Exception {

    /**
     * Creates a new instance of <code>RentalRateNameExistsException</code>
     * without detail message.
     */
    public RentalRateNameExistsException() {
    }

    /**
     * Constructs an instance of <code>RentalRateNameExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RentalRateNameExistsException(String msg) {
        super(msg);
    }
}
