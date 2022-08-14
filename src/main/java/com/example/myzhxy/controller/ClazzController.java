package com.example.myzhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myzhxy.pojo.Clazz;
import com.example.myzhxy.service.ClazzService;
import com.example.myzhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Class Controller")
@RestController
@RequestMapping("/sms/clazzController")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @ApiOperation("Get classes in page")
    @GetMapping("/getClazzsByOpr/{pageNo}/{pageSize}")
    public Result<Object> getClazzWithPageInfo(@ApiParam("Page Number") @PathVariable("pageNo") Integer pageNo,
                                                @ApiParam("Page Size") @PathVariable("pageSize") Integer pageSize,
                                               @ApiParam("Grade Name") Clazz clazz) {
        Page<Clazz> page = new Page<>(pageNo, pageSize);
        IPage<Clazz> pageRs =  clazzService.getClazzByOpr(page, clazz);
        return Result.ok(pageRs);
    }

    @ApiOperation("Get all classes")
    @GetMapping("/getClazzs")
    public Result<Object> getClazzs() {
        List<Clazz> list = clazzService.getAllGrades();
        return Result.ok(list);
    }

    @ApiOperation("Save class")
    @PostMapping("/saveOrUpdateClazz")
    public Result<Object> saveOrUpdateClazz(@ApiParam("Grade body") @RequestBody Clazz clazz) {
        clazzService.saveOrUpdate(clazz);
        return Result.ok();
    }

    @ApiOperation("Delete class")
    @DeleteMapping("/deleteClazz")
    public Result<Object> deleteClazz(@ApiParam("Cluster of class id need to delete") @RequestBody List<Integer> ids) {
        clazzService.removeByIds(ids);
        return Result.ok();
    }
}
