package com.example.parking.error;

public class ReservationNotFound extends Exception {

    public ReservationNotFound() {
        super("Reservation not found");
    }
}
