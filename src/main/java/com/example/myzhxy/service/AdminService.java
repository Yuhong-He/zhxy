package com.example.myzhxy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myzhxy.pojo.Admin;
import com.example.myzhxy.pojo.LoginForm;

public interface AdminService extends IService<Admin> {
    Admin login(LoginForm loginForm);

    Admin getAdminById(Long userId);
}
