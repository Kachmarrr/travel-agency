package com.epam.finaltask.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.epam.finaltask.model.enums.HotelType;
import com.epam.finaltask.model.enums.TourType;
import com.epam.finaltask.model.enums.TransferType;
import com.epam.finaltask.model.enums.TourStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "tour_type")
    private TourType tourType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    private TransferType transferType;

    @Enumerated(EnumType.STRING)
    @Column(name = "hotel_type")
    private HotelType hotelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tour_status")
    private TourStatus status;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "eviction_date")
    private LocalDate evictionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_hot", nullable = false)
    private boolean isHot;


}
