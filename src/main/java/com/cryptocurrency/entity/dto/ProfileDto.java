package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private final Long id;
    private final UserDto user;
    private final String name;
    private final String email;
    private final String phone;
    private final String country;
    private final String address;
    private final String imageId;
}