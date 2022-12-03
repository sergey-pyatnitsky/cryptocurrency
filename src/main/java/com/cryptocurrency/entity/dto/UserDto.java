package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDto {
    private final String username;
    private final String password;
    private final boolean isActive;
    private final Set<AuthorityDto> roles;
}