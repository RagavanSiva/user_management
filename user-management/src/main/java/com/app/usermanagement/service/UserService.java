package com.app.usermanagement.service;

import com.app.usermanagement.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDto saveUser(UserDto userDto);
    UserDto getUser(String email);
    UserDto getUserByUserId(String userId);

    List<UserDto> getAllUserDetails();

    UserDto updateUser(String id, UserDto userDto);

    void deleteUser(String id);

}
