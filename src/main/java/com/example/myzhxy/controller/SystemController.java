package com.example.myzhxy.controller;

import com.example.myzhxy.pojo.Admin;
import com.example.myzhxy.pojo.LoginForm;
import com.example.myzhxy.pojo.Student;
import com.example.myzhxy.pojo.Teacher;
import com.example.myzhxy.service.AdminService;
import com.example.myzhxy.service.StudentService;
import com.example.myzhxy.service.TeacherService;
import com.example.myzhxy.util.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sms/system")
public class SystemController {

    @Autowired
    AdminService adminService;

    @Autowired
    StudentService studentService;

    @Autowired
    TeacherService teacherService;

    @RequestMapping("/getVerifiCodeImage")
    public void getVerifyCodeImage(HttpServletRequest request, HttpServletResponse response) {
        // get image
        BufferedImage verifyCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();

        // get verify code
        String verifyCode = new String(CreateVerifiCodeImage.getVerifiCode());

        // put verify code to session
        HttpSession session = request.getSession();
        session.setAttribute("verificode", verifyCode);

        // respond the image to frontend
        try {
            ImageIO.write(verifyCodeImage, "JPEG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginForm loginForm, HttpServletRequest request) {
        // verify code validate
        HttpSession session = request.getSession();
        String sessionVerificode = (String) session.getAttribute("verificode");
        String loginFormVerifiCode = loginForm.getVerifiCode();
        if("".equals(loginFormVerifiCode) || null == loginFormVerifiCode){
            return Result.fail().message("Verify code was expired, please refresh and try again");
        }
        if(!sessionVerificode.equals(loginFormVerifiCode)) {
            return Result.fail().message("Wrong verify code, please try again.");
        }

        //remove verify code from session
        session.removeAttribute("verificode");

        //validate user by type
        Map<String, Object> map = new LinkedHashMap<>();
        switch(loginForm.getUserType()){
            case 1:
                try {
                    Admin admin = adminService.login(loginForm);
                    if(null != admin){
                        map.put("token", JwtHelper.createToken(admin.getId().longValue(), 1));
                    } else {
                        throw new RuntimeException("Username or Password error");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 2:
                try {
                    Student student = studentService.login(loginForm);
                    if(null != student){
                        map.put("token", JwtHelper.createToken(student.getId().longValue(), 2));
                    } else {
                        throw new RuntimeException("Username or Password error");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 3:
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if(null != teacher){
                        map.put("token", JwtHelper.createToken(teacher.getId().longValue(), 3));
                    } else {
                        throw new RuntimeException("Username or Password error");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
        }
        return Result.fail().message("user not exist");
    }

    @GetMapping("/getInfo")
    public Result<Object> getInfoByToken(@RequestHeader("token") String token) {
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        Map<String, Object> map = new LinkedHashMap<>();
        if(userType == null){
            return Result.build(null, ResultCodeEnum.ILLEGAL_REQUEST);
        }
        switch(userType){
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType", 1);
                map.put("user", admin);
                break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("userType", 2);
                map.put("user", student);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType", 3);
                map.put("user", teacher);
                break;
        }
        return Result.ok(map);
    }

    @ApiOperation("File upload")
    @PostMapping("/headerImgUpload")
    public Result<Object> headerImageUpload(
            @ApiParam("File need to upload") @RequestPart("multipartFile") MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        int i = originalFilename.lastIndexOf(".");
        String newFileName = uuid + originalFilename.substring(i);

        // save file
        String portraitPath = "/Users/yuhong/IdeaProjects/myzhxy/target/classes/public/upload/" + newFileName;
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // response a image url
        String path = "upload/".concat(newFileName);

        return Result.ok(path);
    }

    @ApiOperation("Reset admin password")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result<Object> updatePwd(
            @ApiParam("user token") @RequestHeader("token") String token,
            @ApiParam("old password") @PathVariable("oldPwd") String oldPwd,
            @ApiParam("new password") @PathVariable("newPwd") String newPwd
    ) {
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            Result.fail().message("Token has expired, please login again");
        }
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        oldPwd = MD5.encrypt(oldPwd);
        newPwd = MD5.encrypt(newPwd);
        if(userType != null) {
            switch (userType) {
                case 1:
                    Admin admin = adminService.verifyPassword(userId, oldPwd);
                    if(admin != null){
                        admin.setPassword(newPwd);
                        adminService.saveOrUpdate(admin);
                    } else {
                        return Result.fail().message("Origin password incorrect");
                    }
                    break;
                case 2:
                    Student student = studentService.verifyPassword(userId, oldPwd);
                    if(student != null){
                        student.setPassword(newPwd);
                        studentService.saveOrUpdate(student);
                    } else {
                        return Result.fail().message("Origin password incorrect");
                    }
                    break;
                case 3:
                    Teacher teacher = teacherService.verifyPassword(userId, oldPwd);
                    if(teacher != null){
                        teacher.setPassword(newPwd);
                        teacherService.saveOrUpdate(teacher);
                    } else {
                        return Result.fail().message("Origin password incorrect");
                    }
                    break;
            }
        } else {
            return Result.fail().message("Unexpected error: userType is null");
        }
        return Result.ok();
    }
}
