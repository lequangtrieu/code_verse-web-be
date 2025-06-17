package codeverse.com.web_be.dto.response.CourseResponse.CourseDetail;

import codeverse.com.web_be.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailResponse {
    Course course;
    CourseMoreInfoDTO courseMoreInfo;
}
