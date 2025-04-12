package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.UserRequest.UserCreationRequest;
import codeverse.com.web_be.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userRequestToUser(UserCreationRequest request);
}
