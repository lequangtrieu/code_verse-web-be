package codeverse.com.web_be.service.UserService;

import codeverse.com.web_be.dto.request.UserRequest.UserCreationByAdminRequest;
import codeverse.com.web_be.dto.request.UserRequest.UserUpdateRequest;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserDetailResponse;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.service.IGenericService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IUserService extends IGenericService<User, Long> {
    Optional<User> findByUsername(String username);
    UserResponse getMyInfo();
    void toggleLockUser(Long userId, boolean lock);
    User createUserByAdmin(UserCreationByAdminRequest request);
    Optional<User> findById(Long id);
    UserDetailResponse getUserDetailByAdmin(Long userId);
    UserResponse updateMyInfo(UserUpdateRequest userUpdateRequest);
    UserResponse updateAvatar(MultipartFile file);
    List<UserDetailResponse> getInactiveInstructors();
    void activateInstructor(Long userId);
    void deactivateInstructor(Long userId);
    List<UserResponse> getActiveUsers();
}