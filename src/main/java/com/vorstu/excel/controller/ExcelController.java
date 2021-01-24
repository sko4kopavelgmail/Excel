package com.vorstu.excel.controller;

import com.vorstu.excel.dto.FilterProperties;
import com.vorstu.excel.dto.WorkDayDto;
import com.vorstu.excel.model.GroupEntity;
import com.vorstu.excel.repository.GroupRepo;
import com.vorstu.excel.service.ExcelFilterService;
import com.vorstu.excel.service.ExcelParseService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/excel")
public class ExcelController {

    private final ExcelParseService service;
    private final ExcelFilterService excelService;
    private final GroupRepo groupRepo;

    public ExcelController(ExcelParseService service, ExcelFilterService excelService, GroupRepo groupRepo) {
        this.service = service;
        this.excelService = excelService;
        this.groupRepo = groupRepo;
    }

    @PostMapping("/parse")
    public boolean parseExcelFile(@RequestParam("file") MultipartFile file) {
        return service.parse(file);
    }

    @PostMapping()
    public List<WorkDayDto> findAll(@RequestBody FilterProperties filterProperties) {
        return excelService.findFilteredWorkDays(filterProperties);
    }

    @GetMapping
    public List<GroupEntity> findAllGroups() {
        return groupRepo.findAll();
    }

}
