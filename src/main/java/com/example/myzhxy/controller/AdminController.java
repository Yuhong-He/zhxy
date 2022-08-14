package com.example.myzhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myzhxy.pojo.Admin;
import com.example.myzhxy.service.AdminService;
import com.example.myzhxy.util.MD5;
import com.example.myzhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Admin Controller")
@RestController
@RequestMapping("/sms/adminController")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @ApiOperation("Get admins in page")
    @GetMapping("/getAllAdmin/{pageNo}/{pageSize}")
    public Result<Object> getAllAdmin(@ApiParam("Page Number") @PathVariable("pageNo") Integer pageNo,
                                      @ApiParam("Page Size") @PathVariable("pageSize") Integer pageSize,
                                      @ApiParam("Admin Name") Admin admin) {
        Page<Admin> page = new Page<>(pageNo, pageSize);
        IPage<Admin> pageRs =  adminService.getAdminByOpr(page, admin);
        return Result.ok(pageRs);
    }

    @ApiOperation("Save admin")
    @PostMapping("/saveOrUpdateAdmin")
    public Result<Object> saveOrUpdateAdmin(@ApiParam("Admin body") @RequestBody Admin admin) {
        Integer id = admin.getId();
        if(id == null || 0 == id){
            admin.setPassword(MD5.encrypt(admin.getPassword()));
        }
        adminService.saveOrUpdate(admin);
        return Result.ok();
    }

    @ApiOperation("Delete admin")
    @DeleteMapping("/deleteAdmin")
    public Result<Object> deleteAdmin(@ApiParam("Cluster of teacher id need to delete") @RequestBody List<Integer> ids) {
        adminService.removeByIds(ids);
        return Result.ok();
    }
}
