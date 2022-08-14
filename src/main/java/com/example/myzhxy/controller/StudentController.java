package com.example.myzhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myzhxy.pojo.Student;
import com.example.myzhxy.service.StudentService;
import com.example.myzhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Student Controller")
@RestController
@RequestMapping("/sms/studentController")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @ApiOperation("Get students in page")
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result<Object> getStudentByOpr(@ApiParam("Page Number") @PathVariable("pageNo") Integer pageNo,
                                               @ApiParam("Page Size") @PathVariable("pageSize") Integer pageSize,
                                               @ApiParam("Student Name") Student student) {
        Page<Student> page = new Page<>(pageNo, pageSize);
        IPage<Student> pageRs =  studentService.getClazzByOpr(page, student);
        return Result.ok(pageRs);
    }

    @ApiOperation("Save student")
    @PostMapping("/addOrUpdateStudent")
    public Result<Object> addOrUpdateStudent(@ApiParam("Student body") @RequestBody Student student) {
        studentService.saveOrUpdate(student);
        return Result.ok();
    }

    @ApiOperation("Delete students")
    @DeleteMapping("/delStudentById")
    public Result<Object> deleteStudent(@ApiParam("Cluster of student id need to delete") @RequestBody List<Integer> ids) {
        studentService.removeByIds(ids);
        return Result.ok();
    }
}
