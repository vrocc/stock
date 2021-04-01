package com.roc.spring.component.advice;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author roc
 */
@RestController
@RequestMapping("")
public class HelloController {

    @Value("${spring.application.name:(请设置项目名称🤷)}")
    private String projectName;

    @RequestMapping("")
    public HelloResponse hello() {
        HelloResponse response = new HelloResponse();
        String data = "hello, this is " + projectName + " service!";
        response.setMsg(data);
        return response;
    }

    @Data
    static class HelloResponse {
        private String msg;
    }

}
