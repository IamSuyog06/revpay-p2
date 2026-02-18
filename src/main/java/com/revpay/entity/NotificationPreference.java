package com.revpay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean transactionAlerts = true;

    @Column(nullable = false)
    private boolean moneyRequestAlerts = true;

    @Column(nullable = false)
    private boolean cardChangeAlerts = true;

    @Column(nullable = false)
    private boolean lowBalanceAlerts = true;

    @Column(nullable = false)
    private boolean invoiceAlerts = true;

    @Column(nullable = false)
    private boolean loanAlerts = true;
}
