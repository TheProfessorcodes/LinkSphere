package com.LinkSphere.userService.controller;

import com.LinkSphere.userService.dto.LoginRequestDto;
import com.LinkSphere.userService.dto.SignUpRequestDto;
import com.LinkSphere.userService.dto.UserDto;
import com.LinkSphere.userService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        UserDto userDto=authService.signUp(signUpRequestDto);

        return new  ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> signup(@RequestBody LoginRequestDto loginRequestDto) {
        String token=authService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }

}
