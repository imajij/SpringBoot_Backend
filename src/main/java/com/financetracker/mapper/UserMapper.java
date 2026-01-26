package com.financetracker.mapper;

import com.financetracker.dto.UserProfileDto;
import com.financetracker.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserProfileDto toProfileDto(User user);
}
