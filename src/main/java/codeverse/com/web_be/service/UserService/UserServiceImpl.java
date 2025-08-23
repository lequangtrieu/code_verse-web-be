package codeverse.com.web_be.service.UserService;

import codeverse.com.web_be.dto.request.UserRequest.UserCreationByAdminRequest;
import codeverse.com.web_be.dto.request.UserRequest.UserUpdateRequest;
import codeverse.com.web_be.dto.response.UserResponse.UserDetailResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.*;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.mapper.UserMapper;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.EmailService.EmailServiceSender;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.GenericServiceImpl;
import codeverse.com.web_be.service.NotificationService.INotificationService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final FirebaseStorageService firebaseStorageService;
    private final FunctionHelper functionHelper;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final EmailServiceSender emailSender;
    private final INotificationService notificationService;
    private final CourseRepository courseRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, FirebaseStorageService firebaseStorageService, FunctionHelper functionHelper, CourseEnrollmentRepository courseEnrollmentRepository, CodeSubmissionRepository codeSubmissionRepository, EmailServiceSender emailSender, INotificationService notificationService, CourseRepository courseRepository, LessonProgressRepository lessonProgressRepository, LessonRepository lessonRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.firebaseStorageService = firebaseStorageService;
        this.functionHelper = functionHelper;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.emailSender = emailSender;
        this.notificationService = notificationService;
        this.courseRepository = courseRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonRepository = lessonRepository;
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserResponse response = userMapper.userToUserResponse(user);
        response.setBadges(getBadgesByUser(user));
        response.setTrainingStatus(getTrainingStatus(user.getId()));
        response.setLessonProgressStatus(getLessonProgressStatus(user.getId()));
        return response;
    }

    @Override
    public UserResponse updateMyInfo(UserUpdateRequest userUpdateRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 🔒 Validate name
        if (userUpdateRequest.getName() == null || userUpdateRequest.getName().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_NAME);
        }

        if (userUpdateRequest.getPhoneNumber() != null && !userUpdateRequest.getPhoneNumber().trim().isEmpty()) {
            // ✅ số điện thoại bắt đầu bằng 0 và có đúng 10 chữ số
            String phoneRegex = "^0\\d{9}$";
            if (!userUpdateRequest.getPhoneNumber().matches(phoneRegex)) {
                throw new AppException(ErrorCode.INVALID_PHONE);
            }
        }

        // ✅ Chỉ cập nhật các trường được phép
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
        if (file != null && !file.isEmpty()) {
            try {
                avatar = firebaseStorageService.uploadImage(file);
            } catch (Exception e) {
                log.error("🔥 Failed to upload avatar to Firebase", e);
                throw new RuntimeException("Avatar upload failed", e);
            }
        }
        user.setAvatar(avatar);
        User updatedUser = userRepository.save(user);
        return userMapper.userToUserResponse(updatedUser);
    }

    @Override
    public UserResponse updateQrCode(MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String qrUrl = null;
        if (file != null && !file.isEmpty()) {
            qrUrl = firebaseStorageService.uploadImage(file);
        }

        user.setQrCodeUrl(qrUrl); // 👈 Gán vào trường QR
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

        try {
            if(lock) emailSender.sendUserBannedEmail(user);
            else emailSender.sendUserUnbannedEmail(user);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
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
        userRepository.save(user);

        try {
            emailSender.sendImportedUserWelcomeEmail(user);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        return user;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDetailResponse getUserDetailByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.userToUserDetailResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDetailResponse> getInactiveInstructors() {
        List<User> inactiveInstructors = userRepository.findInactiveInstructors();

        return inactiveInstructors.stream()
                .map(userMapper::userToUserDetailResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void activateInstructor(Long instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setInstructorStatus(InstructorStatus.APPROVED);
        userRepository.save(user);

        try {
            emailSender.sendInstructorApprovalEmail(user);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deactivateInstructor(Long instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setInstructorStatus(InstructorStatus.REJECTED);
        userRepository.save(user);

        try {
            emailSender.sendInstructorRejectionEmail(user);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserResponse> getActiveUsers() {
        List<User> activeUsers = userRepository.findByIsDeletedFalseAndIsVerifiedTrue();
        return activeUsers.stream()
                .map(userMapper::userToUserResponse)
                .toList();
    }

    @Override
    public List<BadgeType> getBadgesByUser(User learner) {
        List<BadgeType> badges = new ArrayList<>();
        if(!learner.getIsDeleted() && learner.getIsVerified()) badges.add(BadgeType.NEW_LEARNER);
        if(courseEnrollmentRepository.existsByUserId(learner.getId())) badges.add(BadgeType.FIRST_COURSE);
        if(codeSubmissionRepository.countByUserId(learner.getId()) >= 10) badges.add(BadgeType.TEN_CODE);
        return badges;
    }

    @Override
    public String getTrainingStatus(Long userId) {
        return codeSubmissionRepository.countTrainingCodeSubmissionsByUserId(userId) +
                "/" + courseRepository.countByStatus(CourseStatus.TRAINING_PUBLISHED);
    }

    @Override
    public String getLessonProgressStatus(Long userId) {
        return lessonProgressRepository.countByUserIdAndStatus(userId, LessonProgressStatus.PASSED) +
                "/" + lessonRepository.countLessonsByUserId(userId);
    }

}