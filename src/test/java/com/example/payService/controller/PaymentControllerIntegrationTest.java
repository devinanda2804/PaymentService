package com.example.payService.controller;

import com.example.payService.dto.PaymentRequest;
import com.example.payService.dto.PaymentResponseDto;
import com.example.payService.feign.BookingClient;
import com.example.payService.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT ,properties = "spring.profiles.active=test")
public class PaymentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private BookingClient bookingClient;


    @BeforeEach
    public void setup() {
        testRestTemplate = testRestTemplate.withBasicAuth("user", "password");
    }

    @Test
    public void bookSeatsTest() {

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(101);
        paymentRequest.setBookingId(200);
        paymentRequest.setAmount(120);


        ResponseEntity<PaymentResponseDto> response = testRestTemplate.postForEntity(
                "/api/user/success", paymentRequest, PaymentResponseDto.class);


        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(101, response.getBody().getUserId());
        assertEquals(200, response.getBody().getBookingId());
        assertEquals("CONFIRMED", response.getBody().getStatus());
        assertEquals(LocalDate.now(), response.getBody().getDate());
    }


    @Test
    public void cancelSeatsTest() {

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(240);
        paymentRequest.setUserId(180);
        paymentRequest.setBookingId(205);


        ResponseEntity<PaymentResponseDto> response = testRestTemplate.postForEntity(
                "/api/user/fail", paymentRequest, PaymentResponseDto.class);


        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(180, response.getBody().getUserId());
        assertEquals(205, response.getBody().getBookingId());
        assertEquals("FAILED", response.getBody().getStatus());
        assertEquals(LocalDate.now(), response.getBody().getDate());
    }

}