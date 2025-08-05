package certification.testtoken.exception;

import certification.testtoken.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 全局异常处理类，@ControllerAdvice指定对example.controller包下的控制器进行异常处理增强
@ControllerAdvice("example.controller")
public class GlobalExceptionHandler {

    // @ExceptionHandler捕获所有Exception类型的异常，作为统一的异常处理入口
    @ExceptionHandler(Exception.class)
    @ResponseBody // 让方法返回值以JSON格式响应给前端
    public Result error(Exception e) {
        // 出现异常时，调用Result的error方法返回统一的错误结果
        e.printStackTrace();
        return Result.error();
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody // 让方法返回值以JSON格式响应给前端
    public Result customError(CustomException e) {
        // 出现自定义异常时，调用Result的error方法返回自定义的错误结果
        e.printStackTrace();
        return Result.error(e.getCode(),e.getMsg());
    }
}