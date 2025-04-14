package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.UserRequest.UserCreationRequest;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.mapper.UserMapper;
import codeverse.com.web_be.service.UserService.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}