package com.example.ticketdestroyer.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime dateTime;

    private String location;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;

}
