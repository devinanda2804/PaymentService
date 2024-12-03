package com.example.payService.feign;

import com.example.payService.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "booking-service", url = "http://localhost:8086/api/bookings") 
public interface BookingClient {

    @PutMapping("/{bookingId}/status")
    boolean updateBookingStatus(@PathVariable("bookingId") Integer bookingId,
                                @RequestParam("status") String status,
                                @RequestHeader("Authorization") String token); 




   @PutMapping("/{bookingId}/cancel")
    void cancelBooking(@PathVariable Integer bookingId,
                       @RequestHeader("Authorization") String authorization);

}
