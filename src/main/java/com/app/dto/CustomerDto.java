package com.app.dto;

import java.time.LocalDateTime;

import lombok.*;

public record CustomerDto (  Long id,
         String code,
         String name,
         String contactPerson,
         String email,
         String phone,
         String address,
         String city,
         String country,
         String customerType,
         LocalDateTime createdAt,
         LocalDateTime updatedAt)
{

}
