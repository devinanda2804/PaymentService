/*
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


}*/


package com.example.payService.service;


import com.example.payService.dto.OrderResponseDto;
import com.example.payService.feign.BookingClient;
import com.example.payService.model.Payment;
import com.example.payService.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
/*

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(@Value("${razorpay.key.id}") String keyId,
                          @Value("${razorpay.secret.key}") String keySecret,
                          PaymentRepository paymentRepository) throws Exception {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
        this.paymentRepository = paymentRepository;
    }

    public OrderResponseDto createOrder(Integer userId, Integer bookingId, Integer amount) throws Exception {
        int amountInPaise = amount * 100;

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + bookingId);

        Order order = razorpayClient.orders.create(orderRequest);

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setBookingId(bookingId);
        payment.setStatus("PENDING");
        payment.setAmount(amount);
        payment.setDate(LocalDate.now());
        paymentRepository.save(payment);

        return new OrderResponseDto(
                order.get("id"),
                order.get("amount"),
                order.get("currency"),
                order.get("status")
        );
    }
    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);

        try {
            Utils.verifyPaymentSignature(attributes, "YOUR_SECRET_KEY");
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void updatePaymentStatus(String orderId, String status) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null) {
            payment.setStatus(status);
            paymentRepository.save(payment);
        }
    }
}
*/


@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final BookingClient bookingServiceClient;
    private final String razorpaySecretKey;

    public PaymentService(@Value("${razorpay.key.id}") String keyId,
                          @Value("${razorpay.secret.key}") String keySecret,
                          PaymentRepository paymentRepository,
                          BookingClient bookingServiceClient) throws Exception {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
        this.razorpaySecretKey = keySecret;
        this.paymentRepository = paymentRepository;
        this.bookingServiceClient = bookingServiceClient;
    }

    public OrderResponseDto createOrder(Integer userId, Integer bookingId, Integer amount) throws Exception {
        int amountInPaise = amount * 100;

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + bookingId);

        Order order = razorpayClient.orders.create(orderRequest);

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setBookingId(bookingId);
        payment.setOrderId(order.get("id"));
        payment.setStatus("PENDING");
        payment.setAmount(amount);
        payment.setDate(LocalDate.now());
        paymentRepository.save(payment);

        return new OrderResponseDto(
                order.get("id"),
                order.get("amount"),
                order.get("currency"),
                order.get("status")
        );
    }

   /* public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);

        try {
            Utils.verifyPaymentSignature(attributes, razorpaySecretKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/
   public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
       try {
           String secret = "ivTCmWwrbBlKHZpyXKHEvHKo"; // Replace with your actual Razorpay secret key
           String payload = razorpayOrderId + "|" + razorpayPaymentId;

           Mac mac = Mac.getInstance("HmacSHA256");
           mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
           byte[] hash = mac.doFinal(payload.getBytes());


           String generatedSignature = Hex.encodeHexString(hash);
           return generatedSignature.equals(razorpaySignature);
       } catch (Exception e) {
           throw new RuntimeException("Error verifying payment signature", e);
       }
   }

    public void updatePaymentStatus(String orderId, String status, String token) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null) {
            payment.setStatus(status);
            paymentRepository.save(payment);

            if ("CONFIRMED".equals(status)) {
                bookingServiceClient.updateBookingStatus(payment.getBookingId(), "SUCCESS", token);
                bookingServiceClient.updateSeats(payment.getBookingId(), false, token);

            }
        }
    }

}
