package com.example.myzhxy.controller;

import com.example.myzhxy.pojo.Admin;
import com.example.myzhxy.pojo.LoginForm;
import com.example.myzhxy.pojo.Student;
import com.example.myzhxy.pojo.Teacher;
import com.example.myzhxy.service.AdminService;
import com.example.myzhxy.service.StudentService;
import com.example.myzhxy.service.TeacherService;
import com.example.myzhxy.util.CreateVerifiCodeImage;
import com.example.myzhxy.util.JwtHelper;
import com.example.myzhxy.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public Result login(@RequestBody LoginForm loginForm, HttpServletRequest request) {
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
}
