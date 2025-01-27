package com.example.parking.controller;

import com.example.parking.data.dto.ReservationRequest;
import com.example.parking.error.AlreadyReservedException;
import com.example.parking.error.MaxUtilizationException;
import com.example.parking.error.ReservationNotFound;
import com.example.parking.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public void makeReservation(@RequestBody @Valid ReservationRequest request) throws MaxUtilizationException, AlreadyReservedException {
        reservationService.makeReservation(request);
    }

    @DeleteMapping
    public void cancelReservation(@RequestBody @Valid ReservationRequest request) throws ReservationNotFound {
        reservationService.cancelReservation(request);
    }
}
