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
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private Booker booker;

    @ManyToOne
    @JoinColumn(name = "promocode_id")
    private Discount discount;

    private LocalDateTime time;

    private String status; // Could be "reserved", "confirmed", "canceled", etc.
}
