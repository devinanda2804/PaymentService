package com.example.payService.service;

import com.example.payService.dto.PaymentResponseDto;
import com.example.payService.feign.BookingClient;
import com.example.payService.model.Payment;
import com.example.payService.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
@Service
public class PaymentService {

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private PaymentRepository paymentRepository;


    /*@Transactional  // Ensures both operations are part of a single transaction
    public PaymentResponseDto successPayment(Integer userId, Integer bookingId, Integer amount) {
        // Step 1: Update the status of the booking in the Booking Service
        boolean isBookingUpdated = bookingClient.updateBookingStatus(bookingId, "CONFIRMED");

        if (!isBookingUpdated) {
            throw new RuntimeException("Booking status update failed.");
        }

        // Step 2: Save the payment details in the Payment table
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setStatus("CONFIRMED");
        payment.setDate(LocalDate.now());
        paymentRepository.save(payment);

        // Step 3: Prepare the response DTO
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setUserId(userId);
        responseDto.setBookingId(bookingId);
        responseDto.setStatus("CONFIRMED");
        responseDto.setDate(payment.getDate());

        return responseDto;
    }*/
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);


    @Transactional  // Ensures both operations are part of a single transaction
    public PaymentResponseDto successPayment(Integer userId, Integer bookingId, Integer amount, String token) {
        try {
            boolean isBookingUpdated = bookingClient.updateBookingStatus(bookingId, "CONFIRMED", token);
            logger.info("Booking update result: {}", isBookingUpdated);// Pass the token

            if (!isBookingUpdated) {
                throw new RuntimeException("Booking status update failed.");
            }

            // Step 2: Save the payment details in the Payment table
            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setStatus("CONFIRMED");
            payment.setDate(LocalDate.now());
            // Log the payment details
            logger.debug("Saving payment: {}", payment);

            paymentRepository.save(payment);  // Save the payment to the database

            // Step 3: Prepare the response DTO
            PaymentResponseDto responseDto = new PaymentResponseDto();
            responseDto.setUserId(userId);
            responseDto.setBookingId(bookingId);
            responseDto.setStatus("CONFIRMED");
            responseDto.setDate(payment.getDate());

            return responseDto;

        } catch (Exception e) {
            logger.error("Error during payment processing: ", e);
            throw new RuntimeException("Failed to process payment", e);
        }


    }
}