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
            logger.info("Calling updateBookingStatus with bookingId={}, status={}, token={}", bookingId, "CONFIRMED", token);

            // Step 1: Update booking status using Feign client
            bookingClient.updateBookingStatus(bookingId, "CONFIRMED", token);
            logger.info("Booking status updated successfully for bookingId={}", bookingId);


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
    @Transactional
    public PaymentResponseDto failedPayment(Integer userId, Integer bookingId, Integer amount, String token) {
        try {
            logger.info("Processing failed payment for bookingId={}, userId={}", bookingId, userId);

            // Step 1: Update booking status to "FAILED" using Feign client
            logger.info("Calling updateBookingStatus with bookingId={}, status={}, token={}", bookingId, "FAILED", token);
            bookingClient.updateBookingStatus(bookingId, "FAILED", token);
            logger.info("Booking status updated to FAILED for bookingId={}", bookingId);

            // Step 2: Cancel booking and make seats available
            logger.info("Calling cancelBooking for bookingId={} to make seats available", bookingId);
            bookingClient.cancelBooking(bookingId, token);
            logger.info("Booking canceled and seats made available for bookingId={}", bookingId);

            // Step 3: Record the failed payment in the Payment table
            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setStatus("FAILED");
            payment.setDate(LocalDate.now());
            paymentRepository.save(payment); // Save the payment record
            logger.info("Payment failure recorded for bookingId={}, userId={}", bookingId, userId);

            // Step 4: Prepare and return the response DTO
            PaymentResponseDto responseDto = new PaymentResponseDto();
            responseDto.setUserId(userId);
            responseDto.setBookingId(bookingId);
            responseDto.setStatus("FAILED");
            responseDto.setDate(payment.getDate());

            return responseDto;

        } catch (Exception e) {
            logger.error("Error during failed payment processing: ", e);
            throw new RuntimeException("Failed to process payment failure", e);
        }
    }


}