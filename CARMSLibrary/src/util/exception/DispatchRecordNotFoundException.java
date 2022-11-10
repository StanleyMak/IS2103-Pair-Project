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
public class DispatchRecordNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>DispatchRecordNotFoundException</code>
     * without detail message.
     */
    public DispatchRecordNotFoundException() {
    }

    /**
     * Constructs an instance of <code>DispatchRecordNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DispatchRecordNotFoundException(String msg) {
        super(msg);
    }
}
