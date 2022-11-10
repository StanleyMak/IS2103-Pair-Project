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
public class DeleteCarCategoryException extends Exception {

    /**
     * Creates a new instance of <code>DeleteCarCategoryException</code> without
     * detail message.
     */
    public DeleteCarCategoryException() {
    }

    /**
     * Constructs an instance of <code>DeleteCarCategoryException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteCarCategoryException(String msg) {
        super(msg);
    }
}
