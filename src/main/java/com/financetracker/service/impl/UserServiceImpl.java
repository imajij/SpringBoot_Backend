package com.financetracker.service.impl;

import com.financetracker.dto.UserProfileDto;
import com.financetracker.entity.User;
import com.financetracker.mapper.UserMapper;
import com.financetracker.repository.UserRepository;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;

    @Override
    public UserProfileDto getCurrentUserProfile() {
        User user = securityUtils.getCurrentUser();
        log.info("Fetching profile for user: {}", user.getEmail());
        return userMapper.toProfileDto(user);
    }

    @Override
    public UserProfileDto updateProfile(UserProfileDto profileDto) {
        User user = securityUtils.getCurrentUser();
        log.info("Updating profile for user: {}", user.getEmail());

        if (profileDto.getFirstName() != null) {
            user.setFirstName(profileDto.getFirstName());
        }
        if (profileDto.getLastName() != null) {
            user.setLastName(profileDto.getLastName());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", user.getEmail());
        return userMapper.toProfileDto(updatedUser);
    }
}
