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



    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);


    @Transactional
    public PaymentResponseDto successPayment(Integer userId, Integer bookingId, Integer amount, String token) {
        try {
            logger.info("Calling updateBookingStatus with bookingId={}, status={}, token={}", bookingId, "CONFIRMED", token);


            bookingClient.updateBookingStatus(bookingId, "CONFIRMED", token);
            logger.info("Booking status updated successfully for bookingId={}", bookingId);


            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setStatus("CONFIRMED");
            payment.setDate(LocalDate.now());

            logger.debug("Saving payment: {}", payment);

            paymentRepository.save(payment);


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

            logger.info("Calling updateBookingStatus with bookingId={}, status={}, token={}", bookingId, "FAILED", token);
            bookingClient.updateBookingStatus(bookingId, "FAILED", token);
            logger.info("Booking status updated to FAILED for bookingId={}", bookingId);

            logger.info("Calling cancelBooking for bookingId={} to make seats available", bookingId);
            bookingClient.cancelBooking(bookingId, token);
            logger.info("Booking canceled and seats made available for bookingId={}", bookingId);

            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setStatus("FAILED");
            payment.setDate(LocalDate.now());
            paymentRepository.save(payment);
            logger.info("Payment failure recorded for bookingId={}, userId={}", bookingId, userId);


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