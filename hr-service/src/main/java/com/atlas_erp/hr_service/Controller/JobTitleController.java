package com.atlas_erp.hr_service.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.JobTitle.JobTitleResponse;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.hr_service.Service.JobTitleService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobTitles")
public class JobTitleController {

    private final JobTitleService jobTitleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobTitleResponse>>> getAllJobTitle() {

        List<JobTitleResponse> data = jobTitleService.getAllJobTitles();

        return ResponseEntity.ok(
                ApiResponse.success("Job titles retrieved successfully", data));
    }
}
