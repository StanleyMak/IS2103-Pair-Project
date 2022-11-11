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
public class CarModelDisabledException extends Exception {

    /**
     * Creates a new instance of <code>CarModelDisabledException</code> without
     * detail message.
     */
    public CarModelDisabledException() {
    }

    /**
     * Constructs an instance of <code>CarModelDisabledException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CarModelDisabledException(String msg) {
        super(msg);
    }
}
