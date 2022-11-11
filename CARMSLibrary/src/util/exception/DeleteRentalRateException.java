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
public class DeleteRentalRateException extends Exception {

    /**
     * Creates a new instance of <code>DeleteRentalRateException</code> without
     * detail message.
     */
    public DeleteRentalRateException() {
    }

    /**
     * Constructs an instance of <code>DeleteRentalRateException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteRentalRateException(String msg) {
        super(msg);
    }
}
