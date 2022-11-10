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
public class DispatchRecordNameExistsException extends Exception {

    /**
     * Creates a new instance of <code>DispatchRecordNameExistsException</code>
     * without detail message.
     */
    public DispatchRecordNameExistsException() {
    }

    /**
     * Constructs an instance of <code>DispatchRecordNameExistsException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DispatchRecordNameExistsException(String msg) {
        super(msg);
    }
}
