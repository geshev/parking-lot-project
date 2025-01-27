package com.example.parking.data.mapper;

import com.example.parking.data.dto.ReservationRequest;
import com.example.parking.data.model.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation fromRequest(ReservationRequest request);
}
