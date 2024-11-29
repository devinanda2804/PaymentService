package com.example.payService.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentResponseDto {

    private Integer userId;
    private Integer bookingId;

    private String status;

    private LocalDate date;


}

