package com.app.usermanagement.controller;

import com.app.usermanagement.exceptions.UserServiceException;
import com.app.usermanagement.model.request.UserDetailsRequestModel;
import com.app.usermanagement.model.response.AddressResponseDto;
import com.app.usermanagement.model.response.ErrorMessages;
import com.app.usermanagement.model.response.UserDetailsResponseModel;
import com.app.usermanagement.service.AddressService;
import com.app.usermanagement.service.UserService;
import com.app.usermanagement.shared.dto.AddressDto;
import com.app.usermanagement.shared.dto.UserDto;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;
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

    @Autowired
    AddressService addressesService;

    @Autowired
    AddressService addressService;
    //save user
    @PostMapping
    public UserDetailsResponseModel saveUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel) throws Exception{

        if (userDetailsRequestModel.getFirstName().isEmpty()) throw new Exception(ErrorMessages.MISSING_REQUIRED_FIELDS.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetailsRequestModel, UserDto.class);
        UserDto createdUserDto = userService.saveUser(userDto);
        UserDetailsResponseModel userDetailsResponseModel = modelMapper.map(createdUserDto, UserDetailsResponseModel.class);
        return userDetailsResponseModel;
    }

    //get user
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "${userController.authorizationHeader.description}",paramType = "header")
    })
    @GetMapping(path = "/{id}")
    public UserDetailsResponseModel getUserByUserId(@PathVariable("id") String id){

        UserDto userDto = userService.getUserByUserId(id);
//        BeanUtils.copyProperties(userDto,userDetailsResponseModel);
        ModelMapper modelMapper = new ModelMapper();
        UserDetailsResponseModel userDetailsResponseModel = modelMapper.map(userDto, UserDetailsResponseModel.class);

        return userDetailsResponseModel;
    }


    //get address details of a particular user
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "${userController.authorizationHeader.description}",paramType = "header")
    })
    @GetMapping(path = "/{id}/address")
    public List<AddressResponseDto> getAllAddressByUserId(@PathVariable("id") String id){
        List<AddressResponseDto> returnValue = new ArrayList<>();
        List<AddressDto> addressDto = addressesService.getAddresses(id);
        if(addressDto != null && !addressDto.isEmpty()) {
            java.lang.reflect.Type listType = new TypeToken<List<AddressResponseDto>>() {
            }.getType();
            returnValue = new ModelMapper().map(addressDto, listType);
        }

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "${userController.authorizationHeader.description}",paramType = "header")
    })
    @GetMapping(path = "/{id}/address/{addressId}")
    public AddressResponseDto getUserAddress(@PathVariable("id") String id,@PathVariable("addressId") String addressId){
        AddressDto returnValue = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();



        return modelMapper.map(returnValue,AddressResponseDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "${userController.authorizationHeader.description}",paramType = "header")
    })
    @GetMapping
    public List<UserDetailsResponseModel> getAllUsers(
            @RequestParam(value = "pageNumber",defaultValue = "1") int pageNumber
            , @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
       List<UserDetailsResponseModel> userDetailsResponseModelList = new ArrayList<>();

       List<UserDto> userDtos = userService.getAllUserDetails(pageNumber,pageSize);

       for (UserDto user : userDtos){
//           UserDetailsResponseModel userDetailsResponseModel = new UserDetailsResponseModel();
           ModelMapper modelMapper = new ModelMapper();

//           BeanUtils.copyProperties(user,userDetailsResponseModel);
           userDetailsResponseModelList.add(modelMapper.map(user, UserDetailsResponseModel.class));
       }

        return userDetailsResponseModelList;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "${userController.authorizationHeader.description}",paramType = "header")
    })
    @PutMapping("/{id}")
    public UserDetailsResponseModel updateUserDetails(@PathVariable("id") String id ,@RequestBody UserDetailsRequestModel userDetailsRequestModel){


//        BeanUtils.copyProperties(userDetailsRequestModel,userDto);
        UserDto updatedUserDto = userService.updateUser(id,new ModelMapper().map(userDetailsRequestModel, UserDto.class));
        UserDetailsResponseModel userDetailsResponseModel = new ModelMapper().map(updatedUserDto, UserDetailsResponseModel.class);
        return userDetailsResponseModel;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "${userController.authorizationHeader.description}",paramType = "header")
    })
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") String id){
        userService.deleteUser(id);
        return "User Deleted Successfully";
    }

}
