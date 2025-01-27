package com.example.parking.data.dto;

import com.example.parking.validation.ValidSpace;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequest(@NotBlank String carPlate, @NotNull @ValidSpace Integer space,
                                 @NotNull LocalDate date, @NotNull @Min(0) @Max(23) Integer time) {
}
