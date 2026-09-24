package com.LinkSphere.userService.service;

import com.LinkSphere.userService.dto.LoginRequestDto;
import com.LinkSphere.userService.dto.SignUpRequestDto;
import com.LinkSphere.userService.dto.UserDto;
import com.LinkSphere.userService.entity.User;
import com.LinkSphere.userService.event.UserCreatedEvent;
import com.LinkSphere.userService.exception.BadRequestException;
import com.LinkSphere.userService.exception.ResourceNotFoundException;
import com.LinkSphere.userService.repository.UserRepository;
import com.LinkSphere.userService.utils.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final KafkaTemplate<Long,UserCreatedEvent> kafkaTemplate;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {
        log.info("SignUpRequestDto={}", signUpRequestDto);
        boolean exists=userRepository.existsByEmail(signUpRequestDto.getEmail());
        if(exists){
            throw new BadRequestException("Email Already Exists");
        }
        User user = modelMapper.map(signUpRequestDto, User.class);
        user.setPassword(BCrypt.hash(signUpRequestDto.getPassword()));
        user=userRepository.save(user);
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();
        kafkaTemplate.send("user_created_topic", userCreatedEvent);
        return modelMapper.map(user, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("LoginRequestDto={}", loginRequestDto);
        User user= userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(()->new ResourceNotFoundException("User not found"));
        boolean isPasswordMatch=BCrypt.match(loginRequestDto.getPassword(),user.getPassword());

        if(!isPasswordMatch){
            throw new BadRequestException("Wrong Password");
        }

        return jwtService.generateAccessToken(user);
    }


}
