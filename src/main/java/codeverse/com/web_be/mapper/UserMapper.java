package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.UserRequest.UserCreationRequest;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Convert request dto -> entity
    User userRequestToUser(UserCreationRequest request);

    // Convert entity -> response dto
    UserResponse userToUserResponse(User user);

    // Áp dụng update dữ liệu từ dto vào entity đã tồn tại
    void updateUserFromRequest(UserCreationRequest request, @MappingTarget User entity);

}
