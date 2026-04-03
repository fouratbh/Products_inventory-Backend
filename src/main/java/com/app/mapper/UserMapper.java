package com.app.mapper;

import java.util.List;

import org.mapstruct.*;


import com.app.dto.UserCreateDto;
import com.app.dto.UserDto;
import com.app.dto.UserUpdateDto;
import com.app.entities.User;


@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    
    @Mapping(target = "roles", source = "roles")
    UserDto toDTO(User user);
    
    List<UserDto> toDTOList(List<User> users);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateDto dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(UserUpdateDto dto, @MappingTarget User user);
}
