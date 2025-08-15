package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.DashboardResponse.InstructorRevenueDTO;
import codeverse.com.web_be.service.InstructorRevenueService.InstructorRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
public class InstructorRevenueController {

    private final InstructorRevenueService instructorRevenueService;

//    @GetMapping("/instructors")
//    public List<InstructorRevenueDTO> getInstructorRevenue() {
//        return instructorRevenueService.getAllInstructorRevenue();
//    }

    @GetMapping("/instructors")
    public List<InstructorRevenueDTO> getInstructorRevenue(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer quarter) {
        return instructorRevenueService.getInstructorRevenue(type, year, month, quarter);
    }



}
