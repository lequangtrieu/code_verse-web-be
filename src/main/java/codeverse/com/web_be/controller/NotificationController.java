package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.NotificationResponse.NotificationResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.service.NotificationService.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationService;

    @GetMapping("/history")
    public ApiResponse<List<NotificationResponse>> getNotificationsReceivedByUserId(@RequestParam String username) {
        List<NotificationResponse> notifications = notificationService.getNotificationsReceivedByUser(username);
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notifications)
                .code(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/{userNotificationId}/user/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long userNotificationId) {
        notificationService.markAsRead(userNotificationId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("/user/mark-all-as-read")
    public ApiResponse<Void> markAllAsRead(@RequestParam String username) {
        notificationService.markAllAsRead(username);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/user/count")
    public ApiResponse<?> getNotificationsCountByUsername(@RequestParam String username) {
        return ApiResponse.builder()
                .result(notificationService.getUnreadNotificationsCountByUser(username))
                .code(HttpStatus.OK.value())
                .build();
    }
}
