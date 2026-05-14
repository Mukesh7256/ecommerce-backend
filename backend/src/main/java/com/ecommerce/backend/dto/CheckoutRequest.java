package com.ecommerce.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// T052: Validate order creation
@Data
public class CheckoutRequest {

    @NotBlank(message = "Delivery address is required!")
    @Size(min = 10, message = "Please enter complete address!")
    private String deliveryAddress;

    @NotBlank(message = "City is required!")
    private String city;

    @NotBlank(message = "State is required!")
    private String state;

    @NotBlank(message = "Pincode is required!")
    @Pattern(
        regexp = "^[1-9][0-9]{5}$",
        message = "Please enter valid 6-digit pincode!"
    )
    private String pincode;

    @NotBlank(message = "Phone number is required!")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Please enter valid 10-digit phone number!"
    )
    private String phone;

    @NotBlank(message = "Payment method is required!")
    private String paymentMethod;
}