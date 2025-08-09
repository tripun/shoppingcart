package com.example.shoppingcart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for User registration and login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for User operations")
public class UserDto {

    @NotBlank(message = "Username cannot be blank")
    @Schema(description = "Unique username for the user.", example = "john.doe")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "Password for the user.", example = "password123")
    private String password;

    @Schema(description = "Role of the user (e.g., USER, ADMIN).", example = "USER")
    private String role;
}
