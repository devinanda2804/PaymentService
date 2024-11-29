package com.example.payService.service;

import com.example.payService.PayServiceApplication;
import com.example.payService.dto.PaymentResponseDto;
import com.example.payService.feign.BookingClient;
import com.example.payService.model.Payment;
import com.example.payService.repository.PaymentRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private BookingClient bookingClient;

    @Mock
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSuccessPayment(){

        Integer userId=101;
        Integer bookingId=200;
        Integer amount=120;
        String token="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInVzZXJJZCI6MTksImlhdCI6MTczMjg0NjQ3MSwiZXhwIjoxNzMyODUwMDcxfQ.t7SkW4Tojtg4BiYdwiwDbiDw6WMfIvaGfPD1NBhZTt8";

        PaymentResponseDto responseDto=new PaymentResponseDto();
        responseDto.setStatus("CONFIRMED");
        responseDto.setDate(LocalDate.now());
        responseDto.setUserId(userId);
        responseDto.setBookingId(bookingId);


        doNothing().when(bookingClient).updateStatus(bookingId,"CONFIRMED",token);

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setStatus("CONFIRMED");
        payment.setDate(LocalDate.now());

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);


        PaymentResponseDto paymentResponseDto=paymentService.successPayment(userId,bookingId,amount,token);

        assertEquals(responseDto.getUserId(),paymentResponseDto.getUserId());
        assertEquals(responseDto.getStatus(),paymentResponseDto.getStatus());
        assertEquals(responseDto.getBookingId(),paymentResponseDto.getBookingId());
        assertEquals(responseDto.getDate(),paymentResponseDto.getDate());

        verify(bookingClient, times(1)).updateStatus(bookingId, "CONFIRMED",token);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    public void failedPayment(){
        Integer userId=101;
        Integer bookingId=200;
        Integer amount=120;

        PaymentResponseDto responseDto=new PaymentResponseDto();
        responseDto.setStatus("FAILED");
        responseDto.setDate(LocalDate.now());
        responseDto.setUserId(userId);
        responseDto.setBookingId(bookingId);

        doNothing().when(bookingClient).updateStatus(bookingId,"FAILED");
        doNothing().when(bookingClient).cancelBooking(bookingId);

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setStatus("FAILED");
        payment.setDate(LocalDate.now());

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponseDto paymentResponseDto=paymentService.cancelPayment(userId,bookingId,amount);

        assertEquals(responseDto.getDate(),paymentResponseDto.getDate());
        assertEquals(responseDto.getUserId(),paymentResponseDto.getUserId());
        assertEquals(responseDto.getBookingId(),paymentResponseDto.getBookingId());
        assertEquals(responseDto.getStatus(),paymentResponseDto.getStatus());

        verify(bookingClient,times(1)).updateStatus(bookingId,"FAILED");
        verify(bookingClient,times(1)).cancelBooking(bookingId);
        verify(paymentRepository,times(1)).save(any(Payment.class));
    }


}
