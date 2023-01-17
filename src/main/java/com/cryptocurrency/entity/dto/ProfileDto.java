package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class ProfileDto {
    private final Long id;
    private final UserDto user;

    @NotBlank(message = "{user.validator.name.empty}")
    @Size(max = 256, message = "{user.validator.name.size}")
    private final String name;
    private final String email;
    private final String phone;
    private final String country;
    private final String address;
    private final String imageId;
}