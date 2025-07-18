package example.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import example.common.Result;

@RestController
public class WebController {

    @GetMapping("/hello")
    public Result hello() {
        // throw new CustomException("400","错误请求，禁止访问");
        return  Result.success("Hello World!") ;
    }
}
