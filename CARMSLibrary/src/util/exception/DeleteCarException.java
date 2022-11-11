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
public class DeleteCarException extends Exception {

    /**
     * Creates a new instance of <code>DeleteCarException</code> without detail
     * message.
     */
    public DeleteCarException() {
    }

    /**
     * Constructs an instance of <code>DeleteCarException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteCarException(String msg) {
        super(msg);
    }
}
