package com.oasis.homehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {

    private Long id;
    private String roleName;
    private String displayName;
    private String description;
    private Integer roleLevel;
    private List<PermissionDTO> permissions;
}

