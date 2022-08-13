package com.example.myzhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myzhxy.pojo.Grade;
import com.example.myzhxy.service.GradeService;
import com.example.myzhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Grade Controller")
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @ApiOperation("Get grades in page")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result<Object> getGradesWithPageInfo(@ApiParam("Page Number") @PathVariable("pageNo") Integer pageNo,
                                                @ApiParam("Page Size") @PathVariable("pageSize") Integer pageSize,
                                                @ApiParam("Grade Name") String gradeName) {
        Page<Grade> page = new Page<>(pageNo, pageSize);
        IPage<Grade> pageRs =  gradeService.getGradeByOpr(page, gradeName);
        return Result.ok(pageRs);
    }

    @ApiOperation("Get all grades")
    @GetMapping("/getGrades")
    public Result<Object> getGrades() {
        List<Grade> list = gradeService.getAllGrades();
        return Result.ok(list);
    }

    @ApiOperation("Save grade")
    @PostMapping("/saveOrUpdateGrade")
    public Result<Object> saveOrUpdateGrade(@ApiParam("Grade body") @RequestBody Grade grade) {
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }

    @ApiOperation("Delete grade")
    @DeleteMapping("/deleteGrade")
    public Result<Object> deleteGrade(@ApiParam("Cluster of grade id need to delete") @RequestBody List<Integer> ids) {
        gradeService.removeByIds(ids);
        return Result.ok();
    }
}
