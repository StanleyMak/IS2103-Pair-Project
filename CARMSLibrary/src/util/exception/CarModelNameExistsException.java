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
public class CarModelNameExistsException extends Exception {

    /**
     * Creates a new instance of <code>CarModelNameExistsException</code>
     * without detail message.
     */
    public CarModelNameExistsException() {
    }

    /**
     * Constructs an instance of <code>CarModelNameExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CarModelNameExistsException(String msg) {
        super(msg);
    }
}
