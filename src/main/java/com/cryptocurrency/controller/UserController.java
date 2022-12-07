package com.cryptocurrency.controller;

import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.dto.ProfileDto;
import com.cryptocurrency.entity.dto.UserDto;
import com.cryptocurrency.exception.IncorrectDataException;
import com.cryptocurrency.mapper.ProfileMapper;
import com.cryptocurrency.service.user.UserService;
import com.cryptocurrency.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileMapper profileMapper;

    @PostMapping("/profile/edit_password")
    @ResponseStatus(HttpStatus.OK)
    public void editUserPass(@RequestBody UserDto userDto, Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword().replace("{bcrypt}", "")))
            throw new IncorrectDataException("old password doesnt equals");

        userService.changeAuthData(user, userDto.getPassword());
    }

    @GetMapping("/profile/get_profile_data")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ProfileDto getProfileData(Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))
                .orElseThrow(() -> new IncorrectDataException("User not found"));
        return profileMapper.toDto(userService.findProfileByUser(user));
    }

    @PostMapping("/profile/edit")
    @ResponseStatus(HttpStatus.OK)
    public void editProfileData(@RequestBody ProfileDto profileDto, Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        userService.editProfileData(user, profileDto);
    }
}

