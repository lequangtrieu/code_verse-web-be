package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.DashboardResponse.CourseRevenueDTO;
import codeverse.com.web_be.service.CourseRevenueService.CourseRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
public class CourseRevenueController {

    private final CourseRevenueService courseRevenueService;

//    @GetMapping("/instructors/{instructorId}/courses")
//    public List<CourseRevenueDTO> getCourseRevenue(@PathVariable Long instructorId) {
//        return courseRevenueService.getCourseRevenue(instructorId);
//    }

    @GetMapping("/instructors/{instructorId}/courses")
    public List<CourseRevenueDTO> getCourseRevenue(
            @PathVariable Long instructorId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer quarter
    ) {
        return courseRevenueService.getCourseRevenueByInstructor(instructorId, type, year, month, quarter);
    }

}

