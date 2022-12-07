package com.cryptocurrency.controller;

import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.exception.IncorrectDataException;
import com.cryptocurrency.service.google.drive.FileManager;
import com.cryptocurrency.service.user.UserService;
import com.cryptocurrency.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileManager fileManager;

    @PostMapping("/uploadFile")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody String uploadFile(@RequestParam(name = "file", required = false) MultipartFile file,
                                    Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication))
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        return userService.editProfileImage(
                fileManager.uploadFile(file, user.getUsername() + "/crypto-profile.png"), user);
    }

    @GetMapping("/downloadFile/{id}")
    public void downloadFile(@PathVariable String id, HttpServletResponse response)
            throws IOException, GeneralSecurityException {
        fileManager.downloadFile(id, response.getOutputStream());
    }
}
