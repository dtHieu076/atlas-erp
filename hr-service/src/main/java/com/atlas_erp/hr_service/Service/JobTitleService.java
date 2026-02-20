package com.atlas_erp.hr_service.Service;

import com.atlas_erp.hr_service.Dto.Response.JobTitle.JobTitleResponse;

import java.util.List;

public interface JobTitleService {

    // Lấy toàn bộ chức danh
    List<JobTitleResponse> getAllJobTitles();

}