/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author hanyang
 */
@Entity
public class OwnCustomerEntity extends CustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    private String password;

    public OwnCustomerEntity() {
        super();
    }
    
    public OwnCustomerEntity(String email, String password) {
        super(email);
        this.password = password;
    }
    
    @Override
    public Long getCustomerID() {
        return super.getCustomerID(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCustomerID(Long customerID) {
        super.setCustomerID(customerID);
    }
    
    @Override
    public String getEmail() {
        return super.getEmail();
    }
    
    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
