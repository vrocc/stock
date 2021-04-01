package com.roc.financial.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
public class JsonpController {
    @Autowired
    private RestTemplate restTemplate;

    private static final Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build();


    @RequestMapping({"jsonp"})
    @ResponseStatus
    public ResponseEntity<String> jsonp(String url,
                                        @RequestParam(defaultValue = "callback") String callback,
                                        HttpServletResponse response) {
        String result = null;

        try {
            result = cache.getIfPresent(url);
        } catch (Exception ignored) {
        }

        if (!StringUtils.hasText(result)) {
            result = this.restTemplate.getForObject(url, String.class);
            cache.put(url, Objects.requireNonNull(result));
        }

        MediaType mediaType = new MediaType("application", "javascript", StandardCharsets.UTF_8);
        response.setContentType(mediaType.toString());
        response.setStatus(200);
        System.out.println(url);
        CacheControl cc = CacheControl.maxAge(12L, TimeUnit.HOURS);
        return ResponseEntity.ok().contentType(mediaType).cacheControl(cc).body(callback + "(" + result + ");");
    }
}
