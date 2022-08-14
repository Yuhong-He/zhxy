package com.example.myzhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myzhxy.pojo.Teacher;
import com.example.myzhxy.service.TeacherService;
import com.example.myzhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Teacher Controller")
@RestController
@RequestMapping("/sms/teacherController")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @ApiOperation("Get teachers in page")
    @GetMapping("/getTeachers/{pageNo}/{pageSize}")
    public Result<Object> getTeachers(@ApiParam("Page Number") @PathVariable("pageNo") Integer pageNo,
                                          @ApiParam("Page Size") @PathVariable("pageSize") Integer pageSize,
                                          @ApiParam("Student Name") Teacher teacher) {
        Page<Teacher> page = new Page<>(pageNo, pageSize);
        IPage<Teacher> pageRs =  teacherService.getClazzByOpr(page, teacher);
        return Result.ok(pageRs);
    }

    @ApiOperation("Save teacher")
    @PostMapping("/saveOrUpdateTeacher")
    public Result<Object> saveOrUpdateTeacher(@ApiParam("Teacher body") @RequestBody Teacher teacher) {
        teacherService.saveOrUpdate(teacher);
        return Result.ok();
    }

    @ApiOperation("Delete teacher")
    @DeleteMapping("/deleteTeacher")
    public Result<Object> deleteTeacher(@ApiParam("Cluster of teacher id need to delete") @RequestBody List<Integer> ids) {
        teacherService.removeByIds(ids);
        return Result.ok();
    }
}
