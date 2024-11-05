package net.sonerapp.jwtauth.interfaces.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sonerapp.jwtauth.application.dto.OkDto;
import net.sonerapp.jwtauth.application.dto.AuthControllerDto.LoginRequestDto;
import net.sonerapp.jwtauth.application.dto.AuthControllerDto.LoginResponseDto;
import net.sonerapp.jwtauth.application.exceptions.UserDeactivatedException;
import net.sonerapp.jwtauth.application.service.JwtService;
import net.sonerapp.jwtauth.infrastructure.security.jwt.JwtUtils;

@RestController
@RequestMapping("/api/v1/jwt")
@Tag(name = "JWT")
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    @Operation(summary = "Authenticate user and return JWT access and refresh token", description = "Authenticates the user using username and password. Returns a JWT access and refresh Token for the user as http-only cookies. The access token expires in 10min and the refresh in 2d.", responses = {
            @ApiResponse(responseCode = "200", description = "Authentication successfull", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication Failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request Body", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
    })
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginData) {
        return jwtService.processLogin(loginData.username(), loginData.password());
    }

    @Operation(summary = "Reauthorize the user", description = "Uses the refresh token in the cookie to generate a new access token", responses = {
            @ApiResponse(responseCode = "200", description = "Reauthorization successfull", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Reauthorization Failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Cookie", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
    }, security = @SecurityRequirement(name = "refreshAuth"))
    @PostMapping("/refresh")
    public ResponseEntity<OkDto> reAuthorize(
            @CookieValue(required = true, name = JwtUtils.REFRESH_COOKIE_KEY) String refreshToken) {
        return jwtService.processReAuthorization(refreshToken);
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> authError(AuthenticationException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Authentication Failed");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();

    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> noUser(UserDeactivatedException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("User Deactivated");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();

    }

}
