package com.cryptocurrency.entity.dto;

import com.cryptocurrency.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorityDto {
    private final Role role;
}