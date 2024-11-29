
package com.example.payService.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentRequest {
    private Integer userId;
    private Integer bookingId;
    private Integer amount;

}
