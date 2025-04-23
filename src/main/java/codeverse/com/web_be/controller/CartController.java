package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.AuthenRequest.AuthenticationRequest;
import codeverse.com.web_be.dto.response.AuthenResponse.AuthenticationResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    @GetMapping("/cartDetail")
    ResponseEntity<String> authenticate(@RequestParam  String username){
        return ResponseEntity.ok("");
    }
}
