package com.financetracker.service;

import com.financetracker.dto.UserProfileDto;

public interface UserService {

    UserProfileDto getCurrentUserProfile();

    UserProfileDto updateProfile(UserProfileDto profileDto);
}
