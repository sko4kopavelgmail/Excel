package com.vorstu.excel.controller;

import com.vorstu.excel.dto.FilterProperties;
import com.vorstu.excel.model.GroupEntity;
import com.vorstu.excel.model.WorkDayEntity;
import com.vorstu.excel.repository.GroupRepo;
import com.vorstu.excel.service.ExcelParseService;
import com.vorstu.excel.service.ExcelFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelParseService service;
    private final ExcelFilterService excelService;
    private final GroupRepo groupRepo;

    @PostMapping("/parse")
    public boolean parseExcelFile(@RequestParam("file") MultipartFile file) {
        return service.parse(file);
    }

    @PostMapping()
    public List<WorkDayEntity> findAll(@RequestBody FilterProperties filterProperties) {
        return excelService.findFilteredWorkDays(filterProperties);
    }

    @GetMapping
    public List<GroupEntity> findAllGroups() {
        return groupRepo.findAll();
    }

}
