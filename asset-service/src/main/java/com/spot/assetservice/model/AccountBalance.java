package com.spot.assetservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "account_balances")
@Getter
@Setter
public class AccountBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Use IDENTITY for MySQL/Auto-increment column
    private Long id;
    private String userId;
    private String currency;
    private Double balance;
    private Double frozenBalance;// newly added frozen balance

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")
    private Date lastUpdated;

    @PrePersist
    @PreUpdate
    private void onUpdate() {
        lastUpdated = new Date();  // Set lastUpdated to the current time on persist/update
    }
}