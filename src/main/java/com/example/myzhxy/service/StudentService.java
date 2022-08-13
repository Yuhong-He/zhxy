package com.example.myzhxy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myzhxy.pojo.LoginForm;
import com.example.myzhxy.pojo.Student;

public interface StudentService extends IService<Student> {
    Student login(LoginForm loginForm);

    Student getStudentById(Long userId);
}
