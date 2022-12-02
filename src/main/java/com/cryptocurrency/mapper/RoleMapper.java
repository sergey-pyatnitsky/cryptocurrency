package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.Authority;
import com.cryptocurrency.entity.dto.AuthorityDto;
import com.cryptocurrency.entity.enums.Role;
import org.mapstruct.Mapper;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public class RoleMapper {

    public static Set<Authority> toModal(AuthorityDto AuthorityDto) {
        return setAllRoles(AuthorityDto.getRole());
    }

    public static AuthorityDto toDTO(Set<Authority> roles) {
        return AuthorityDto.builder().role(getMaxRole(roles)).build();
    }

    private static Set<Authority> setAllRoles(Role role) {
        Set<Authority> authorities = new HashSet<>();
        for (int roleLoop = Role.values().length - 1; roleLoop >= 0; --roleLoop) {
            authorities.add(Authority.builder().name(Role.values()[roleLoop].name()).build());
            if (Role.values()[roleLoop].equals(role)) break;
        }
        return authorities;
    }

    private static Role getMaxRole(Set<Authority> roles) {
        for (int roleLoop = 0; roleLoop < Role.values().length; ++roleLoop) {
            for(Authority authority: roles){
                if(authority.getName().equals(Role.values()[roleLoop].name()))
                    return Role.values()[roleLoop];
            }
        }
        return null;
    }
}
