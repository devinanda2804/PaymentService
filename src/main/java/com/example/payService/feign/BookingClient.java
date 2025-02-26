package com.example.payService.feign;

import com.example.payService.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
/*@FeignClient(name = "booking-service", url = "http://localhost:8086/api/bookings") // Replace with your actual URL
public interface BookingClient {

    @PutMapping("/{bookingId}/status")
    boolean updateBookingStatus(@PathVariable("bookingId") Integer bookingId,
                                @RequestParam("status") String status,
                                @RequestHeader("Authorization") String token); // Add Authorization header
}*/
@FeignClient(name = "booking-service", url = "http://localhost:8086/api/bookings", configuration = FeignClientConfig.class)
public interface BookingClient {

    @PutMapping("/{bookingId}/status")
    void updateBookingStatus(@PathVariable("bookingId") Integer bookingId,
                             @RequestParam("status") String status,
                             @RequestHeader("Authorization") String token);

    @PutMapping("/{bookingId}/seats")
    void updateSeats(@PathVariable("bookingId") Integer bookingId,
                     @RequestParam("available") boolean available,
                     @RequestHeader("Authorization") String token);


/*
    @PutMapping("/{bookingId}/cancel")
    void cancelBooking(@PathVariable Integer bookingId,
                       @RequestHeader("Authorization") String authorization);*/
}
