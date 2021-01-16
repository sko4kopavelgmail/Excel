package com.vorstu.excel.controller;

import com.vorstu.excel.dto.FilterProperties;
import com.vorstu.excel.model.WorkDayEntity;
import com.vorstu.excel.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService service;

    @PostMapping("/parse")
    public boolean parseExcelFile(@RequestParam("file") MultipartFile file) {
        return service.parse(file);
    }

    @PostMapping()
    public List<WorkDayEntity> findAll(@RequestBody FilterProperties filterProperties) {
        return service.findFilteredWorkDays(filterProperties);
    }

}
