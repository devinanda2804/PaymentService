package com.example.payService.controller;

import com.example.payService.dto.OrderRequestDto;
import com.example.payService.dto.PaymentRequest;
import com.example.payService.dto.PaymentResponseDto;
import com.example.payService.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
/*

@RestController
@RequestMapping("/api/payment")
public class PaymentController {


    @Autowired
    private PaymentService paymentService;

   */
/* @PostMapping("/success")

    public ResponseEntity<PaymentResponseDto> bookSeats(@RequestBody PaymentRequest paymentRequest) {
            PaymentResponseDto paymentResponseDto = paymentService.successPayment(
                    paymentRequest.getUserId(),
                    paymentRequest.getBookingId(),
                    paymentRequest.getAmount()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }*//*


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


    @PostMapping("/fail")
    public ResponseEntity<PaymentResponseDto> cancelSeats(@RequestBody PaymentRequest paymentRequest,@RequestHeader("Authorization") String token) {
        PaymentResponseDto paymentResponseDto = paymentService.failedPayment(
                paymentRequest.getUserId(),
                paymentRequest.getBookingId(),
                paymentRequest.getAmount(),token
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }


}
*/



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


import com.example.payService.dto.OrderResponseDto;
import com.example.payService.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {


    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto orderRequest) {
        try {
            OrderResponseDto response = paymentService.createOrder(
                    orderRequest.getUserId(),
                    orderRequest.getBookingId(),
                    orderRequest.getAmount()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
        }
    }




    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload,
                                           @RequestHeader("Authorization") String token) {
        logger.info("Received payment verification request for orderId: {}", payload.get("razorpay_order_id"));
        try {
            boolean isVerified = paymentService.verifyPayment(
                    payload.get("razorpay_order_id"),
                    payload.get("razorpay_payment_id"),
                    payload.get("razorpay_signature")
            );

            if (isVerified) {
                logger.info("Payment verified for orderId: {}", payload.get("razorpay_order_id"));
                paymentService.updatePaymentStatus(payload.get("razorpay_order_id"), "CONFIRMED", token);
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("verified", true);
                }});


            } else {
                logger.warn("Payment verification failed for orderId: {}", payload.get("razorpay_order_id"));
                logger.info("payment_id is:{}",payload.get("razorpay_payment_id"));
                paymentService.updatePaymentStatus(payload.get("razorpay_order_id"), "FAILED", token);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying payment: " + e.getMessage());
        }
    }

}


