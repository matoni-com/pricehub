package com.matoni.pricehub.controller;

import com.matoni.pricehub.controller.dto.AuthenticateUserRequest;
import com.matoni.pricehub.controller.dto.AuthenticateUserResponse;
import com.matoni.pricehub.service.dto.AuthenticateUserCommand;
import com.matoni.pricehub.service.dto.AuthenticateUserResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthenticateUserMapper {
  AuthenticateUserCommand toCommand(AuthenticateUserRequest authenticateUserRequest);

  AuthenticateUserResponse toResponse(AuthenticateUserResult authenticateUserResult);
}
