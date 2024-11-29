package com.example.payService.feign;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class BookingClientTest {

    @Mock
    private BookingClient bookingClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testUpdateStatusWithAuthorization() {
        // Arrange
        Integer bookingId = 1;
        String status = "confirmed";
        String authorization = "BeyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInVzZXJJZCI6MTksImlhdCI6MTczMjg0NjQ3MSwiZXhwIjoxNzMyODUwMDcxfQ.t7SkW4Tojtg4BiYdwiwDbiDw6WMfIvaGfPD1NBhZTt8";

        // Act
        bookingClient.updateStatus(bookingId, status, authorization);

        verify(bookingClient, times(1)).updateStatus(bookingId, status, authorization);
    }

    @Test
    public void testCancelBooking() {

        Integer bookingId = 1;
        String status = "failed";

        bookingClient.updateStatus(bookingId, status);


        bookingClient.cancelBooking(bookingId);

        verify(bookingClient, times(1)).cancelBooking(bookingId);
    }
}
