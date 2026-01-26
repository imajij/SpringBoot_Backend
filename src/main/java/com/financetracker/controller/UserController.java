package com.financetracker.controller;

import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.UserProfileDto;
import com.financetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile() {
        log.info("Profile request received");
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(@RequestBody UserProfileDto profileDto) {
        log.info("Profile update request received");
        UserProfileDto updatedProfile = userService.updateProfile(profileDto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
    }
}
