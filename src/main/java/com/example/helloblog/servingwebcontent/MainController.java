package com.example.helloblog.servingwebcontent;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/blog")
    @ResponseBody
    public String show() {
        return "blog";
    }
}
