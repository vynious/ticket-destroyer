package com.example.ticketdestroyer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Booking {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private Booker booker;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    private LocalDateTime time;

    private String status; // Could be "reserved", "confirmed", "canceled", etc.
}
