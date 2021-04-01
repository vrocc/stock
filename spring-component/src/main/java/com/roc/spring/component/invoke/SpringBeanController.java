package com.roc.spring.component.invoke;

import com.roc.spring.component.advice.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * TODO: Add security check
 * TODO: Add invoke code template
 *
 * @author apple
 */
@RestController
@RequestMapping("spring")
public class SpringBeanController {

    @Autowired
    private SpringBeanRemoteInvokeService invokeService;

    @RequestMapping("beans")
    public List<SpringBeanRemoteInvokeService.BeanInfo> getSpringBeans() {
        return invokeService.getSpringBeans();
    }

    @RequestMapping("beans/{beanName}/methods")
    public List<SpringBeanRemoteInvokeService.SimpleMethodDescription> getBeanMethods(@PathVariable("beanName") String beanName) throws UnsupportedEncodingException {
        String decodeBeanName = URLDecoder.decode(beanName, "utf-8");
        return invokeService.getBeanMethods(decodeBeanName);
    }

    @RequestMapping("beans/{beanName}/methods/{methodName}")
    public ResultVo invokeBeanMethod(@PathVariable("beanName") String beanName,
                                     @PathVariable("methodName") String methodName,
                                     @RequestParam LinkedHashMap<String, String> paramType2ValueMap,
                                     @RequestHeader("signature") String signature) throws UnsupportedEncodingException {
        beanName = URLDecoder.decode(beanName, "utf-8");
        Object invokeResult = invokeService.invokeBeanMethod(beanName, methodName, paramType2ValueMap, signature);
        return ResultVo.successBuilder().data(invokeResult).build();
    }
}
