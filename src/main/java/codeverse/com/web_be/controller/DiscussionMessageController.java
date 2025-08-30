package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.DiscussionRequest.DiscussionMessageResponse;
import codeverse.com.web_be.dto.request.DiscussionRequest.DiscussionRequest;
import codeverse.com.web_be.entity.DiscussionMessage;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.repository.DiscussionMessageRepository;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.repository.UserRepository;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.NotificationService.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/discussions")
@RequiredArgsConstructor
public class DiscussionMessageController {

    private final DiscussionMessageRepository discussionRepo;
    private final LessonRepository lessonRepo;
    private final UserRepository userRepo;
    private final INotificationService notificationService;
    private final FunctionHelper functionHelper;

    private DiscussionMessageResponse toDto(DiscussionMessage msg) {
        if (msg.getIsDeleted()) return null;

        return DiscussionMessageResponse.builder()
                .id(msg.getId())
                .messageText(msg.getMessageText())
                .originalMessage(msg.getOriginalMessage())
                .avatar(msg.getUser().getAvatar())
                .userId(msg.getUser().getId())
                .authorEmail(msg.getUser().getUsername())
                .createdAt(msg.getCreatedAt())
                .replies(msg.getReplies() != null
                        ? msg.getReplies().stream()
                        .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                        .map(this::toDto)
                        .filter(Objects::nonNull)
                        .toList()
                        : List.of())
                .build();
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody DiscussionRequest request) {
        if (functionHelper.isOffensive(request.getMessageText())) {
            return ResponseEntity.badRequest()
                    .body("Your comment contains offensive or inappropriate language. Please revise it.");
        }

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Lesson lesson = lessonRepo.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        DiscussionMessage message = DiscussionMessage.builder()
                .user(user)
                .lesson(lesson)
                .isDeleted(false)
                .messageText(request.getMessageText())
                .parentMessage(null)
                .build();

        discussionRepo.save(message);
        return ResponseEntity.ok(toDto(message));
    }

    @PostMapping("/{parentId}/reply")
    public ResponseEntity<?> replyToComment(@PathVariable Long parentId, @RequestBody DiscussionRequest request) {
        if (functionHelper.isOffensive(request.getMessageText())) {
            return ResponseEntity.badRequest()
                    .body("Your reply contains offensive or inappropriate language. Please revise it.");
        }

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Lesson lesson = lessonRepo.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        DiscussionMessage parent = discussionRepo.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent message not found"));

        DiscussionMessage reply = DiscussionMessage.builder()
                .user(user)
                .lesson(lesson)
                .isDeleted(false)
                .parentMessage(parent)
                .messageText(request.getMessageText())
                .build();

        discussionRepo.save(reply);

        if (!Objects.equals(reply.getUser().getId(), parent.getUser().getId())) {
            notificationService.notifyUsers(List.of(parent.getUser()),
                    user,
                    "Discussion Reply",
                    "<p>" + user.getName() + " has replied to your comment. " +
                            "<a href=\"https://code-verse-web-fe.vercel.app/course/" +
                            lesson.getCourseModule().getCourse().getId() +
                            "/" + (parent.getUser().getRole().equals(UserRole.INSTRUCTOR)
                            ? "view" : "learn")
                            + "?lesson=" + lesson.getId()
                            + "&commentId=" + reply.getId()
                            + "\">View comment >></a></p>");
        }
        return ResponseEntity.ok(toDto(reply));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getCommentsByLesson(@PathVariable Long lessonId) {
        List<DiscussionMessage> messages = discussionRepo.findAllByLessonIdAndParentMessageIsNullOrderByCreatedAtDesc(lessonId);
        List<DiscussionMessageResponse> result = messages.stream()
                .map(this::toDto)
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody DiscussionRequest request) {
        if (functionHelper.isOffensive(request.getMessageText())) {
            return ResponseEntity.badRequest()
                    .body("Your updated comment contains offensive or inappropriate language. Please revise it.");
        }

        DiscussionMessage msg = discussionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        msg.setOriginalMessage(msg.getMessageText());
        msg.setMessageText(request.getMessageText());
        discussionRepo.save(msg);
        return ResponseEntity.ok(toDto(msg));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        DiscussionMessage msg = discussionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        msg.setIsDeleted(true);
        discussionRepo.save(msg);
        return ResponseEntity.ok().build();
    }

}
