package com.example.parking.data.repo;

import com.example.parking.data.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Integer countAllByDateAndTime(LocalDate date, Integer time);

    Optional<Reservation> findBySpaceAndDateAndTime(Integer space, LocalDate date, Integer time);
}
