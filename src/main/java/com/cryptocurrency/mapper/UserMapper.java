package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = AuthorityMapper.class)
public interface UserMapper {

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "authority", expression = "java(RoleMapper.toDTO(user.getRoles()))")
    UserDto toDto(User user);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "roles", expression = "java(RoleMapper.toModal(userDto.getAuthority()))")
    User toModal(UserDto userDto);

    List<UserDto> toDtoList(List<User> userList);

    List<User> toModalList(List<UserDto> userDtoList);
}
