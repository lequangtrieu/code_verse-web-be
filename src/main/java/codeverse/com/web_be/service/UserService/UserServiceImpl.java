package codeverse.com.web_be.service.UserService;

import codeverse.com.web_be.dto.request.UserRequest.UserCreationByAdminRequest;
import codeverse.com.web_be.dto.request.UserRequest.UserUpdateRequest;
import codeverse.com.web_be.dto.response.UserResponse.UserDetailResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.mapper.UserMapper;
import codeverse.com.web_be.repository.UserRepository;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final FirebaseStorageService firebaseStorageService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, FirebaseStorageService firebaseStorageService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.firebaseStorageService = firebaseStorageService;
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.userToUserResponse(user);
    }

    @Override
    public UserResponse updateMyInfo(UserUpdateRequest userUpdateRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // Chỉ cập nhật các trường được phép
        user.setName(userUpdateRequest.getName());
        user.setBio(userUpdateRequest.getBio());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return userMapper.userToUserResponse(updatedUser);
    }

    @Override
    public UserResponse updateAvatar(MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String avatar = null;
        if(file != null && !file.isEmpty()) {
            avatar = firebaseStorageService.uploadImage(file);
        }
        user.setAvatar(avatar);
        User updatedUser = userRepository.save(user);
        return userMapper.userToUserResponse(updatedUser);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<User> findAll() {
        return super.findAll();
    }

    @PostAuthorize("returnObject.get().username == authentication.name")
    @Override
    public Optional<User> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public User save(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void toggleLockUser(Long userId, boolean lock) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsDeleted(lock); // true = locked
        userRepository.save(user);
    }

    @Override
    public User createUserByAdmin(UserCreationByAdminRequest request) {
        // Bắt buộc role là LEARNER dù request có thay đổi
        request.setRole(UserRole.LEARNER);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.userCreationByAdminRequestToUser(request);
        // Mã hóa password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsVerified(true);  // User tạo từ admin import mặc định verified
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDetailResponse getUserDetailByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.userToUserDetailResponse(user);
    }

}