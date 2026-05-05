package com.example.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    @NotBlank(message = "Name is required")
    private String name;

    private String mobile;
}
