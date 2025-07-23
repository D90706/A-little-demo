package example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import example.common.Result;
/**
 * 文件相关的控制层
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @PostMapping("/upload")
    public Result upload() {
        
        return Result.success();//这里以后会返回网络路径
    }
    
}
