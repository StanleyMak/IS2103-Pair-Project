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
public class DeleteReservationException extends Exception {

    /**
     * Creates a new instance of <code>DeleteReservationException</code> without
     * detail message.
     */
    public DeleteReservationException() {
    }

    /**
     * Constructs an instance of <code>DeleteReservationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteReservationException(String msg) {
        super(msg);
    }
}
