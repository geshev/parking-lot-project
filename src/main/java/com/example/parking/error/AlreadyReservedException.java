package com.example.parking.error;

public class AlreadyReservedException extends Exception {

    public AlreadyReservedException() {
        super("Space has already been reserved");
    }
}
