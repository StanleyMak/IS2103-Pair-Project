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
public class DeleteCarModelException extends Exception {

    /**
     * Creates a new instance of <code>DeleteCarModelException</code> without
     * detail message.
     */
    public DeleteCarModelException() {
    }

    /**
     * Constructs an instance of <code>DeleteCarModelException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteCarModelException(String msg) {
        super(msg);
    }
}
