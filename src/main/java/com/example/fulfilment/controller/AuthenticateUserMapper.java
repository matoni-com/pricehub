package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.AuthenticateUserRequest;
import com.example.fulfilment.controller.dto.AuthenticateUserResponse;
import com.example.fulfilment.service.dto.AuthenticateUserCommand;
import com.example.fulfilment.service.dto.AuthenticateUserResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthenticateUserMapper {
    AuthenticateUserCommand toCommand(AuthenticateUserRequest authenticateUserRequest);
    AuthenticateUserResponse toResponse(AuthenticateUserResult authenticateUserResult);
}
