package com.cryptocurrency.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private Long id;
    private UserDto user;

    @NotBlank(message = "{user.validator.name.empty}")
    @Size(max = 256, message = "{user.validator.name.size}")
    private String name;
    private String email;
    private String phone;
    private String country;
    private String address;
    private String imageId;
}