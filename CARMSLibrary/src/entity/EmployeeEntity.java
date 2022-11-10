/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import util.enumeration.EmployeeAccessRightEnum;

/**
 *
 * @author stonley
 */
@Entity
public class EmployeeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeID;
    
    //@Column(nullable = false)
    private String name;
    
    //@Column(nullable = false)
    private String username;
    
    //@Column(nullable = false)
    private String password; 
   
    //@NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeAccessRightEnum employeeAccessRight;
    
    @OneToOne
    private DispatchRecordEntity dispatchRecord;
    
    //@NotNull
    //@ManyToOne(optional = false)
    //@JoinColumn(nullable = false)
    @ManyToOne
    private OutletEntity outlet;

    public EmployeeEntity() {
    }
    
    public EmployeeEntity(String name, OutletEntity outlet, EmployeeAccessRightEnum employeeAccessRight) {
        this.name = name;
        this.employeeAccessRight = employeeAccessRight;
        this.outlet = outlet;
    }

    public EmployeeEntity(String name, OutletEntity outlet, EmployeeAccessRightEnum employeeAccessRight, String username, String password) {
        this.name = name;
        this.employeeAccessRight = employeeAccessRight;
        this.outlet = outlet;
        this.username = username;
        this.password = password;
    }
   
    public Long getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Long employeeID) {
        this.employeeID = employeeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeeAccessRightEnum getEmployeeAccessRight() {
        return employeeAccessRight;
    }

    public void setEmployeeAccessRight(EmployeeAccessRightEnum employeeAccessRight) {
        this.employeeAccessRight = employeeAccessRight;
    }

    public DispatchRecordEntity getDispatchRecord() {
        return dispatchRecord;
    }

    public void setDispatchRecord(DispatchRecordEntity dispatchRecord) {
        this.dispatchRecord = dispatchRecord;
    }

    public OutletEntity getOutlet() {
        return outlet;
    }

    public void setOutlet(OutletEntity outlet) {
        this.outlet = outlet;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeID != null ? employeeID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeID fields are not set
        if (!(object instanceof EmployeeEntity)) {
            return false;
        }
        EmployeeEntity other = (EmployeeEntity) object;
        if ((this.employeeID == null && other.employeeID != null) || (this.employeeID != null && !this.employeeID.equals(other.employeeID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.EmployeeEntity[ id=" + employeeID + " ]";
    }
    
}
