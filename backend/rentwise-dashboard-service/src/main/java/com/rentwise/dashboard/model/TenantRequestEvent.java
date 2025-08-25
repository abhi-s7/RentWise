package com.rentwise.dashboard.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TenantRequestEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long requestId;
    private Long requestedByUserId;
    private String status; // CREATED, APPROVED, REJECTED
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime timestamp;
    
    public TenantRequestEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public TenantRequestEvent(Long requestId, Long requestedByUserId, String status, 
                             String email, String firstName, String lastName) {
        this.requestId = requestId;
        this.requestedByUserId = requestedByUserId;
        this.status = status;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }
    
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
    
    public Long getRequestedByUserId() {
        return requestedByUserId;
    }
    
    public void setRequestedByUserId(Long requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

