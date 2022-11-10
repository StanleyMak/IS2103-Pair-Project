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
public class CarCategoryNameExistsException extends Exception {

    /**
     * Creates a new instance of <code>CarCategoryNameExistsException</code>
     * without detail message.
     */
    public CarCategoryNameExistsException() {
    }

    /**
     * Constructs an instance of <code>CarCategoryNameExistsException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CarCategoryNameExistsException(String msg) {
        super(msg);
    }
}
