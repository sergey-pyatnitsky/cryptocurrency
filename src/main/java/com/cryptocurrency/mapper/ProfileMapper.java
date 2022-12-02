package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.Profile;
import com.cryptocurrency.entity.dto.ProfileDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ProfileMapper {

    ProfileDto toDto(Profile profile);

    Profile toModal(ProfileDto profileDto);

    List<ProfileDto> toDtoList(List<Profile> profileList);

    List<Profile> toModalList(List<ProfileDto> profileDtoList);
}
