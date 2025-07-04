package codeverse.com.web_be.dto.response.CourseResponse.CourseDetail;

import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.entity.Lesson;
import lombok.Data;

import java.util.List;

@Data
public class CourseModuleMoreInfoDTO {
    CourseModule courseModule;
    Integer totalDuration;
    List<Lesson> lessons;
}
