package net.sonerapp.jwtauth.application.dto.UserControllerDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetDto(
        @Size(min = 1, max = 150, message = "Invalid token") String token,
        @Size(min = 8, max = 100, message = "Password must be between 8 - 100 characters") @NotBlank(message = "The Password Field can not be blank") String password,
        @Size(min = 8, max = 100, message = "Password must be between 8 - 100 characters") @NotBlank(message = "The RePassword Field can not be blank") String rePassword) {

}
