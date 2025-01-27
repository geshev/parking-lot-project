package com.example.parking.error;

public class MaxUtilizationException extends Exception {

    public MaxUtilizationException() {
        super("Maximum utilization reached");
    }
}
