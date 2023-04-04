package com.cryptocurrency.controller;

import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.dto.ProfileDto;
import com.cryptocurrency.entity.dto.UserDto;
import com.cryptocurrency.entity.enums.Role;
import com.cryptocurrency.exception.IncorrectDataException;
import com.cryptocurrency.exception.OperationExecutionException;
import com.cryptocurrency.mapper.ProfileMapper;
import com.cryptocurrency.service.user.UserService;
import com.cryptocurrency.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileMapper profileMapper;

    @PostMapping("/profile/edit_password/{username}")
    @ResponseStatus(HttpStatus.OK)
    public void editUserPass(@RequestBody UserDto userDto, @RequestParam("old_pass") String pass,
                             @PathVariable String username) {
        User user = userService.find(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(pass, user.getPassword().replace("{bcrypt}", "")))
            throw new BadCredentialsException("old password doesnt equals");

        userService.changeAuthData(user, userDto.getPassword());
    }

    @GetMapping("/profile/{username}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ProfileDto getProfileDataByUsername(@PathVariable String username) {
        User user = userService.find(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));
        return profileMapper.toDto(userService.findProfileByUser(user));
    }

    @GetMapping("/profile/get_profile_data")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ProfileDto getProfileData(Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))
                .orElseThrow(() -> new IncorrectDataException("User not found"));
        return profileMapper.toDto(userService.findProfileByUser(user));
    }

    @PostMapping("/profile/edit/{username}")
    @ResponseStatus(HttpStatus.OK)
    public void editProfileData(@RequestBody ProfileDto profileDto, @PathVariable String username) {
        User user = userService.find(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        userService.editProfileData(user, profileDto);
    }

    @GetMapping("/get_all")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<ProfileDto> getAllProfiles() {
        return profileMapper.toDtoList(userService.findAllProfile());
    }

    @GetMapping("/get_all/active={isActive}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<ProfileDto> getAllProfilesByActive(@PathVariable boolean isActive) {
        return profileMapper.toDtoList(userService.findAllProfileByActiveStatus(isActive));
    }

    @GetMapping("/get_all/role={role}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<ProfileDto> getAllProfilesByRole(@PathVariable Role role) {
        return profileMapper.toDtoList(userService.findByRole(role));
    }

    @PutMapping("/change_role/{username}/{role}")
    @ResponseStatus(HttpStatus.OK)
    public void changeUserRole(@PathVariable String username, @PathVariable Role role) {
        User user = userService.find(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));
        userService.changeUserRole(user, role);
    }

    @PutMapping("/activate/{username}/{isActive}")
    @ResponseStatus(HttpStatus.OK)
    public void changeUserActiveStatus(@PathVariable String username, @PathVariable boolean isActive) {
        User user = userService.find(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));
        if(!userService.changeActiveStatus(user, isActive))
            throw new OperationExecutionException("user activation change failed");
    }

    @DeleteMapping("/remove/{username}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUser(@PathVariable String username) {
        User user = userService.find(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        if(!userService.remove(user))
            throw new OperationExecutionException("User Deletion Error");
    }
}

