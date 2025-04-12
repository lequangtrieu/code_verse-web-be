package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.UserRequest.UserCreationRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@CrossOrigin("*")
public class UserController {

    IUserService userService;
    UserMapper userMapper;

    // Lấy tất cả users - trả về Response DTO
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        log.info("Authorities: {}", authentication.getAuthorities());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        List<UserResponse> users = userService.findAll().stream()
                .map(userMapper::userToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Lấy user theo id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::userToUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Tạo mới user sử dụng DTO Request (UserCreationRequest)
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreationRequest request) {
        User userToCreate = userMapper.userRequestToUser(request);
        User createdUser = userService.save(userToCreate);
        return new ResponseEntity<>(userMapper.userToUserResponse(createdUser), HttpStatus.CREATED);
    }

    // Cập nhật user
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

    // Delete user theo id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}