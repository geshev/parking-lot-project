package com.example.parking.error;

import com.example.parking.data.dto.ReservationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ReservationError>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ReservationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ReservationError(fieldError.getField() + " - " + fieldError.getDefaultMessage()))
                .toList();
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ReservationError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ReservationError(ex.getMessage()));
    }

    @ExceptionHandler(MaxUtilizationException.class)
    public ResponseEntity<ReservationError> handleMaxUtilization(MaxUtilizationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ReservationError(ex.getMessage()));
    }

    @ExceptionHandler(AlreadyReservedException.class)
    public ResponseEntity<ReservationError> handleAlreadyReserved(AlreadyReservedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ReservationError(ex.getMessage()));
    }

    @ExceptionHandler(ReservationNotFound.class)
    public ResponseEntity<ReservationError> handleReservationNotFound(ReservationNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReservationError(ex.getMessage()));
    }
}
