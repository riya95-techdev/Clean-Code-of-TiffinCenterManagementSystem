package com.example.tiffin_center_management.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tiffin_center_management.payload.ApiResponse;
import com.example.tiffin_center_management.payload.LoginRequest;
import com.example.tiffin_center_management.payload.SignupRequest;
import com.example.tiffin_center_management.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService service;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupRequest req) {
        service.signup(req);
        Map<String,Object> data = new HashMap<>();
        data.put("name", req.getName());
        data.put("email", req.getEmail());
        data.put("role",req.getRole());
        return ResponseEntity.ok(new ApiResponse("User registered", data));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest req,
                                             HttpServletResponse response) {

        String token = service.login(req);

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
        		.httpOnly(true)
                .secure(false)   // true in production (HTTPS)
                .path("/")
                .sameSite("Lax")
                .maxAge(86400)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("email", req.getEmail());
        return ResponseEntity.ok(new ApiResponse("Login success", data));
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse> logout(@CookieValue(name="jwt",required=false) String token ,HttpServletResponse response) {

    	// Agar token null hai, matlab user logged in hi nahi hai
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse("You are not logged in!", null));
        }

        // Agar token hai, toh cookie clear karein
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Expire immediately
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse("Logout success", null));
    }
	
}
