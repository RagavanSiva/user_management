package com.app.usermanagement.controller;

import com.app.usermanagement.exceptions.UserServiceException;
import com.app.usermanagement.model.request.UserDetailsRequestModel;
import com.app.usermanagement.model.response.ErrorMessages;
import com.app.usermanagement.model.response.UserDetailsResponseModel;
import com.app.usermanagement.service.UserService;
import com.app.usermanagement.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;
    //save user
    @PostMapping
    public UserDetailsResponseModel saveUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel) throws Exception{

        if (userDetailsRequestModel.getFirstName().isEmpty()) throw new Exception(ErrorMessages.MISSING_REQUIRED_FIELDS.getErrorMessage());
        UserDetailsResponseModel userDetailsResponseModel = new UserDetailsResponseModel();
        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetailsRequestModel,userDto);

        UserDto createdUserDto = userService.saveUser(userDto);

        BeanUtils.copyProperties(createdUserDto,userDetailsResponseModel);

        return userDetailsResponseModel;
    }

    //get user
    @GetMapping(path = "/{id}")
    public UserDetailsResponseModel getAllUserByUserId(@PathVariable("id") String id){
        UserDetailsResponseModel userDetailsResponseModel = new UserDetailsResponseModel();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto,userDetailsResponseModel);

        return userDetailsResponseModel;
    }

    @GetMapping
    public List<UserDetailsResponseModel> getAllUsers(
            @RequestParam(value = "pageNumber",defaultValue = "1") int pageNumber
            , @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
       List<UserDetailsResponseModel> userDetailsResponseModelList = new ArrayList<>();

       List<UserDto> userDtos = userService.getAllUserDetails(pageNumber,pageSize);

       for (UserDto user : userDtos){
           UserDetailsResponseModel userDetailsResponseModel = new UserDetailsResponseModel();
           BeanUtils.copyProperties(user,userDetailsResponseModel);
           userDetailsResponseModelList.add(userDetailsResponseModel);
       }

        return userDetailsResponseModelList;
    }

    @PutMapping("/{id}")
    public UserDetailsResponseModel updateUserDetails(@PathVariable("id") String id ,@RequestBody UserDetailsRequestModel userDetailsRequestModel){
        UserDetailsResponseModel userDetailsResponseModel = new UserDetailsResponseModel();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequestModel,userDto);
        UserDto updatedUserDto = userService.updateUser(id,userDto);
        BeanUtils.copyProperties(updatedUserDto,userDetailsResponseModel);
        return userDetailsResponseModel;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") String id){
        userService.deleteUser(id);
        return "User Deleted Successfully";
    }

}
