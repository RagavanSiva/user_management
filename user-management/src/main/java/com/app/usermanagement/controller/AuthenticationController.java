package com.app.usermanagement.controller;

import com.app.usermanagement.model.request.LoginRequestModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @ApiOperation("User Login")
    @ApiResponses({
            @ApiResponse(code = 200,message = "Response Body",
                    responseContainer = "<JWT Token>",
                    response = String.class
            )
    })
    @PostMapping("users/login")
    public void swaggerLogin(@RequestBody LoginRequestModel loginRequestModel){
        throw new IllegalStateException("This method should not be called. This method implemented in spring security");
    }
}
