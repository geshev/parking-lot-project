package com.example.parking;

import com.example.parking.data.dto.ReservationRequest;
import com.example.parking.data.mapper.ReservationMapperImpl;
import com.example.parking.data.model.Reservation;
import com.example.parking.data.repo.ReservationRepository;
import com.example.parking.error.AlreadyReservedException;
import com.example.parking.error.MaxUtilizationException;
import com.example.parking.error.ReservationNotFound;
import com.example.parking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    private static final Integer TEST_CAPACITY = 10;
    private static final Integer TEST_UTILIZATION_PERCENTAGE = 50;
    private static final Integer COUNT_UNDER_UTILIZATION = 2;
    private static final Integer COUNT_UTILIZATION = 5;

    private static final Integer TEST_SPACE = 2;
    private static final String TEST_CAR_PLATE = "AA 8888 BB";
    private static final LocalDate TEST_DATE = LocalDate.of(2025, 1, 25);
    private static final Integer TEST_TIME = 9;

    private static final ReservationRequest TEST_RESERVATION_REQUEST =
            new ReservationRequest(TEST_CAR_PLATE, TEST_SPACE, TEST_DATE, TEST_TIME);
    private static final Reservation TEST_RESERVATION =
            new Reservation(null, TEST_CAR_PLATE, TEST_SPACE, TEST_DATE, TEST_TIME);

    @Mock
    private ReservationRepository reservationRepository;

    @Spy
    private ReservationMapperImpl reservationMapper;

    private ReservationService reservationService;

    @BeforeEach
    void serviceSetup() {
        reservationService =
                new ReservationService(reservationRepository, reservationMapper, TEST_CAPACITY, TEST_UTILIZATION_PERCENTAGE);
    }

    @Test
    void testMakeReservation() throws AlreadyReservedException, MaxUtilizationException {
        when(reservationRepository.findBySpaceAndDateAndTime(TEST_SPACE, TEST_DATE, TEST_TIME))
                .thenReturn(Optional.empty());
        when(reservationRepository.countAllByDateAndTime(TEST_DATE, TEST_TIME)).thenReturn(COUNT_UNDER_UTILIZATION);

        reservationService.makeReservation(TEST_RESERVATION_REQUEST);

        verify(reservationMapper, times(1)).fromRequest(TEST_RESERVATION_REQUEST);
        verify(reservationRepository, times(1))
                .findBySpaceAndDateAndTime(TEST_SPACE, TEST_DATE, TEST_TIME);
        verify(reservationRepository, times(1))
                .countAllByDateAndTime(TEST_DATE, TEST_TIME);
        verify(reservationRepository, times(1))
                .save(argThat(new ReservationArgumentMatcher(TEST_RESERVATION)));
    }

    @Test
    void testMakeReservationAlreadyExists() throws AlreadyReservedException, MaxUtilizationException {
        when(reservationRepository.findBySpaceAndDateAndTime(TEST_SPACE, TEST_DATE, TEST_TIME))
                .thenReturn(Optional.of(TEST_RESERVATION));

        reservationService.makeReservation(TEST_RESERVATION_REQUEST);

        verify(reservationMapper, times(1)).fromRequest(TEST_RESERVATION_REQUEST);
        verify(reservationRepository, times(1))
                .findBySpaceAndDateAndTime(TEST_SPACE, TEST_DATE, TEST_TIME);
        verify(reservationRepository, never()).countAllByDateAndTime(any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testMakeReservationAlreadyExistsNoMatch() {
        Reservation nonMatchingReservation =
                new Reservation(null, TEST_CAR_PLATE + "test", TEST_SPACE, TEST_DATE, TEST_TIME);

        when(reservationRepository.findBySpaceAndDateAndTime(TEST_SPACE, TEST_DATE, TEST_TIME))
                .thenReturn(Optional.of(nonMatchingReservation));

        assertThrows(AlreadyReservedException.class,
                () -> reservationService.makeReservation(TEST_RESERVATION_REQUEST));

        verify(reservationMapper, times(1)).fromRequest(TEST_RESERVATION_REQUEST);
        verify(reservationRepository, times(1))
                .findBySpaceAndDateAndTime(TEST_SPACE, TEST_DATE, TEST_TIME);
        verify(reservationRepository, never()).countAllByDateAndTime(any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testMakeReservationMaxUtilization() {
        when(reservationRepository.findBySpaceAndDateAndTime(TEST_SPACE, TEST_DATE, TEST_TIME))
                .thenReturn(Optional.empty());
        when(reservationRepository.countAllByDateAndTime(TEST_DATE, TEST_TIME)).thenReturn(COUNT_UTILIZATION);

        assertThrows(MaxUtilizationException.class,
                () -> reservationService.makeReservation(TEST_RESERVATION_REQUEST));

        verify(reservationMapper, times(1)).fromRequest(TEST_RESERVATION_REQUEST);
        verify(reservationRepository, times(1))
                .findBySpaceAndDateAndTime(TEST_SPACE, TEST_DATE, TEST_TIME);
        verify(reservationRepository, times(1)).countAllByDateAndTime(TEST_DATE, TEST_TIME);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCancelReservation() throws ReservationNotFound {
        when(reservationRepository
                .findBySpaceAndDateAndTimeAndCarPlate(TEST_SPACE, TEST_DATE, TEST_TIME, TEST_CAR_PLATE))
                .thenReturn(Optional.of(TEST_RESERVATION));

        reservationService.cancelReservation(TEST_RESERVATION_REQUEST);

        verify(reservationRepository, times(1))
                .findBySpaceAndDateAndTimeAndCarPlate(TEST_SPACE, TEST_DATE, TEST_TIME, TEST_CAR_PLATE);
        verify(reservationRepository, times(1))
                .delete(argThat(new ReservationArgumentMatcher(TEST_RESERVATION)));
    }

    @Test
    void testCancelReservationNotFound() {
        when(reservationRepository
                .findBySpaceAndDateAndTimeAndCarPlate(TEST_SPACE, TEST_DATE, TEST_TIME, TEST_CAR_PLATE))
                .thenReturn(Optional.empty());

        assertThrows(ReservationNotFound.class,
                () -> reservationService.cancelReservation(TEST_RESERVATION_REQUEST));

        verify(reservationRepository, times(1))
                .findBySpaceAndDateAndTimeAndCarPlate(TEST_SPACE, TEST_DATE, TEST_TIME, TEST_CAR_PLATE);
        verify(reservationRepository, never()).delete(any());
    }

    @RequiredArgsConstructor
    private static class ReservationArgumentMatcher implements ArgumentMatcher<Reservation> {

        private final Reservation value;

        @Override
        public boolean matches(Reservation reservation) {
            return Objects.equals(reservation.getCarPlate(), value.getCarPlate())
                    && Objects.equals(reservation.getSpace(), value.getSpace())
                    && Objects.equals(reservation.getDate(), value.getDate())
                    && Objects.equals(reservation.getTime(), value.getTime());
        }
    }
}
