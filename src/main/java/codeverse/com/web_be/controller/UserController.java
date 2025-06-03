package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.UserRequest.UserCreationByAdminRequest;
import codeverse.com.web_be.dto.request.UserRequest.UserCreationRequest;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserDetailResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.mapper.UserMapper;
import codeverse.com.web_be.service.UserService.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import codeverse.com.web_be.dto.request.UserRequest.LockUserRequest;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    IUserService userService;
    UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAll().stream()
                .map(userMapper::userToUserResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::userToUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreationRequest request) {
        User userToCreate = userMapper.userRequestToUser(request);
        User createdUser = userService.save(userToCreate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.userToUserResponse(createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserCreationRequest request) {
        return userService.findById(id)
                .map(existingUser -> {
                    userMapper.updateUserFromRequest(request, existingUser);
                    User updatedUser = userService.update(existingUser);
                    return ResponseEntity.ok(userMapper.userToUserResponse(updatedUser));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<String>> lockOrUnlockUser(
            @PathVariable Long id,
            @RequestBody LockUserRequest request
    ) {
        userService.toggleLockUser(id, request.isLock());
        String status = request.isLock() ? "User locked" : "User unlocked";
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .result(status)
                .build());
    }

    @PostMapping("/import")
    public ResponseEntity<List<UserResponse>> importUsers(@RequestBody List<UserCreationByAdminRequest> requests) {
        List<UserResponse> createdUsers = requests.stream()
                .map(request -> {
                    // Luôn gán role LEARNER trong service
                    User user = userService.createUserByAdmin(request);
                    return userMapper.userToUserResponse(user);
                })
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
    }

    @GetMapping("/admin/detailUser/{id}")
    public ResponseEntity<UserDetailResponse> getUserDetailForAdmin(@PathVariable Long id) {
        UserDetailResponse detail = userService.getUserDetailByAdmin(id);
        return ResponseEntity.ok(detail);
    }

}