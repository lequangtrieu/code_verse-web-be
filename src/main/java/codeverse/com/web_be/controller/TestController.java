package codeverse.com.web_be.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@CrossOrigin("*")
public class TestController {

    @GetMapping("/test")
    public String get(){
        return "Test";
    }

    @PostMapping("/test")
    public String post(){
        return "Test";
    }
}
