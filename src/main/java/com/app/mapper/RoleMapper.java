package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.RoleCreateDto;
import com.app.dto.RoleDTO;
import com.app.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    
    List<RoleDTO> toDTOList(List<Role> roles);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleCreateDto dto);
}
