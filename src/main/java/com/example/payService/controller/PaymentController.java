package com.example.payService.controller;

import com.example.payService.dto.PaymentRequest;
import com.example.payService.dto.PaymentResponseDto;
import com.example.payService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {


    @Autowired
    private PaymentService paymentService;

   /* @PostMapping("/success")

    public ResponseEntity<PaymentResponseDto> bookSeats(@RequestBody PaymentRequest paymentRequest) {
            PaymentResponseDto paymentResponseDto = paymentService.successPayment(
                    paymentRequest.getUserId(),
                    paymentRequest.getBookingId(),
                    paymentRequest.getAmount()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }*/

    @PostMapping("/success")
    public ResponseEntity<PaymentResponseDto> processPayment(@RequestBody PaymentRequest paymentRequest,
                                                             @RequestHeader("Authorization") String token) {
        // Pass the token to the service to update the booking status
        PaymentResponseDto responseDto = paymentService.successPayment(
                paymentRequest.getUserId(),
                paymentRequest.getBookingId(),
                paymentRequest.getAmount(),
                token // Pass the token to the service method
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    /*@PostMapping("/fail")
*/
   /* public ResponseEntity<PaymentResponseDto> cancelSeats(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponseDto paymentResponseDto = paymentService.cancelPayment(
                paymentRequest.getUserId(),
                paymentRequest.getBookingId(),
                paymentRequest.getAmount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }
*/


}
