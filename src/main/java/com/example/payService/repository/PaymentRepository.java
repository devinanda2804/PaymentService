package com.example.payService.repository;

import com.example.payService.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {


    Payment findByOrderId(String orderId);
}
