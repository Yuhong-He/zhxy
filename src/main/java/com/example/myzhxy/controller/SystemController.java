package com.example.myzhxy.controller;

import com.example.myzhxy.util.CreateVerifiCodeImage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/sms/system")
public class SystemController {

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
}
