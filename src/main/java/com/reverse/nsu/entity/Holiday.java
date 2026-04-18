package com.reverse.nsu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "HOLIDAY")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holidayId")
    private Integer id;

    @Column(name = "holidayDate", nullable = false)
    private LocalDate holidayDate;

    @Column(name = "holidayName", length = 100, nullable = false)
    private String holidayName;

    @Column(name = "isHoliday", nullable = false)
    private Boolean isHoliday;

    @Column(name = "year", nullable = false)
    private Short year;
}
