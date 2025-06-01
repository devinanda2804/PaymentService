package com.example.payService.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class OrderResponseDto {
    private String orderId;
    private int amount;
    private String currency;
    private String status;
}
