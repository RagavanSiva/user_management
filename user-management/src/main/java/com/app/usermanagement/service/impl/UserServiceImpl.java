package com.app.usermanagement.service.impl;

import com.app.usermanagement.entity.UserEntity;
import com.app.usermanagement.model.response.ErrorMessage;
import com.app.usermanagement.model.response.ErrorMessages;
import com.app.usermanagement.repository.UserRepository;
import com.app.usermanagement.service.UserService;
import com.app.usermanagement.shared.dto.AddressDto;
import com.app.usermanagement.shared.dto.UserDto;
import com.app.usermanagement.shared.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto saveUser(UserDto userDto) {
        UserEntity checkUserEntity = userRepository.findByEmail(userDto.getEmail());
        if (checkUserEntity != null) throw new RuntimeException("Record already exists");

        for (int i = 0; i < userDto.getAddresses().size(); i++){
            AddressDto addressDto = userDto.getAddresses().get(i);
            addressDto.setUserDetails(userDto);
            addressDto.setAddressId(utils.generateAddressId(30));
            userDto.getAddresses().set(i,addressDto);
        }
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity =    modelMapper.map(userDto, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(publicUserId);
        UserEntity storedUserEntity = userRepository.save(userEntity);
        UserDto savedUserDto = modelMapper.map(storedUserEntity, UserDto.class);


        return savedUserDto;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        UserDto userDto = new ModelMapper().map(userEntity,UserDto.class);
//        BeanUtils.copyProperties(userEntity, userDto);

        return userDto;
    }

    @Override
    public List<UserDto> getAllUserDetails(int pageNumber, int pageSize) {
        List<UserDto> userDto = new ArrayList<>();
        if (pageNumber > 0) pageNumber = pageNumber - 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserEntity> userEntities = userRepository.findAll(pageable);
        long count = userRepository.count();
        System.out.println("count" + count);
        List<UserEntity> pageableUserEntity = userEntities.getContent();
        for (UserEntity userEntity : pageableUserEntity) {
//            UserDto user = new UserDto();
//            BeanUtils.copyProperties(userEntity, user);

            userDto.add(new ModelMapper().map(userEntity,UserDto.class));
        }
        return userDto;
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {

        UserEntity userEntity = userRepository.findByUserId(id);

        if (userEntity == null) throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        userRepository.save(userEntity);
        UserDto updatedUser = new ModelMapper().map(userEntity, UserDto.class);
//        BeanUtils.copyProperties(userEntity, updatedUser);

        return updatedUser;
    }

    @Override
    public void deleteUser(String id) {
        UserDto deletedUser = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);

        if (userEntity == null) throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
