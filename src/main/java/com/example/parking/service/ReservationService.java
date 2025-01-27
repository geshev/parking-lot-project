package com.example.parking.service;

import com.example.parking.data.dto.ReservationRequest;
import com.example.parking.data.mapper.ReservationMapper;
import com.example.parking.data.model.Reservation;
import com.example.parking.data.repo.ReservationRepository;
import com.example.parking.error.AlreadyReservedException;
import com.example.parking.error.MaxUtilizationException;
import com.example.parking.error.ReservationNotFound;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class ReservationService {

    private final Integer maxUtilization;

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationRepository reservationRepository, ReservationMapper reservationMapper,
                              @Value("${application.parking.capacity}") int capacity,
                              @Value("${application.parking.max.utilization-percentage}") Integer maxUtilizationPercentage) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;

        maxUtilization = (capacity * maxUtilizationPercentage) / 100;
    }

    public void makeReservation(ReservationRequest request) throws MaxUtilizationException, AlreadyReservedException {
        Reservation reservation = reservationMapper.fromRequest(request);

        Optional<Reservation> existingReservation =
                reservationRepository.findBySpaceAndDateAndTime(reservation.getSpace(), reservation.getDate(), reservation.getTime());
        if (existingReservation.isPresent()) {
            if (!existingReservation.get().getCarPlate().equals(reservation.getCarPlate())) {
                throw new AlreadyReservedException();
            }
        } else {
            if (reservationRepository.countAllByDateAndTime(reservation.getDate(), reservation.getTime()) < maxUtilization) {
                reservationRepository.save(reservation);
            } else {
                throw new MaxUtilizationException();
            }
        }
    }

    public void cancelReservation(ReservationRequest request) throws ReservationNotFound {
        Optional<Reservation> existingReservation =
                reservationRepository.findBySpaceAndDateAndTimeAndCarPlate(request.space(), request.date(),
                        request.time(), request.carPlate());
        if (existingReservation.isPresent()) {
            reservationRepository.delete(existingReservation.get());
        } else {
            throw new ReservationNotFound();
        }
    }
}
