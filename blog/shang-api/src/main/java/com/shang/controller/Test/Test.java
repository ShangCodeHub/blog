package com.shang.controller.Test;

import org.springframework.web.bind.annotation.*;

/**
 * @author: quequnlong
 * @date: 2025/1/11
 * @description:
 */
@RestController
@RequestMapping("/Test")

public class Test {
    @GetMapping
    public String Test(){
        return "Test";
    }
    @GetMapping("Test2")
    public String Test2(){
        return "Test2";
    }
}