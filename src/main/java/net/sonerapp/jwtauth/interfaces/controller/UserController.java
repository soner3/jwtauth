package net.sonerapp.jwtauth.interfaces.controller;

import java.util.stream.Stream;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import net.sonerapp.jwtauth.application.dto.UnauthorizedDto;
import net.sonerapp.jwtauth.application.dto.UserControllerDto.ActivateUserDto;
import net.sonerapp.jwtauth.application.dto.UserControllerDto.CreateUserDto;
import net.sonerapp.jwtauth.application.dto.UserControllerDto.EmailDto;
import net.sonerapp.jwtauth.application.dto.UserControllerDto.PasswordResetDto;
import net.sonerapp.jwtauth.application.dto.UserControllerDto.UserDto;
import net.sonerapp.jwtauth.core.exceptions.UserController.EmailDoesNotExistException;
import net.sonerapp.jwtauth.core.exceptions.UserController.EmailExistsException;
import net.sonerapp.jwtauth.core.exceptions.UserController.InvalidUserTokenTypeException;
import net.sonerapp.jwtauth.core.exceptions.UserController.NoStrongPasswordException;
import net.sonerapp.jwtauth.core.exceptions.UserController.PasswordIsNullException;
import net.sonerapp.jwtauth.core.exceptions.UserController.PasswordsDoNotMatchException;
import net.sonerapp.jwtauth.core.exceptions.UserController.TokenAlreadyUsedException;
import net.sonerapp.jwtauth.core.exceptions.UserController.TokenExpiredException;
import net.sonerapp.jwtauth.core.exceptions.UserController.UnknownTokenException;
import net.sonerapp.jwtauth.core.exceptions.UserController.UserEnabledException;
import net.sonerapp.jwtauth.core.exceptions.UserController.UsernameExistsException;
import net.sonerapp.jwtauth.core.model.User;
import net.sonerapp.jwtauth.core.service.UserService;
import net.sonerapp.jwtauth.infrastructure.annotations.RoleValidator;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    private final ConversionService conversionService;

    public UserController(UserService userService, ConversionService conversionService) {
        this.userService = userService;
        this.conversionService = conversionService;
    }

    @Operation(summary = "Create User", description = "Creates a user from the given data in the request body", responses = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkDto.class))),
            @ApiResponse(responseCode = "400", description = "User creation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/public/create")
    public ResponseEntity<OkDto> createUser(@RequestBody @Valid CreateUserDto userData) {
        userService.createUser(userData.username(), userData.email(), userData.password(), userData.rePassword(),
                userData.firstname(), userData.lastname());
        return ResponseEntity.ok(new OkDto("User created successfully. An activation mail has been sent"));
    }

    @Operation(summary = "Activate User", description = "Activates the user by validating the given token in the request body", responses = {
            @ApiResponse(responseCode = "200", description = "Activation successfull", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkDto.class))),
            @ApiResponse(responseCode = "400", description = "Activation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/public/activate")
    public ResponseEntity<OkDto> activateUser(@RequestBody @Valid ActivateUserDto tokenData) {
        userService.activateUser(tokenData.token());
        return ResponseEntity.ok(new OkDto("User activated successfully"));
    }

    @Operation(summary = "Resends user activation mail", description = "Resends a activation mail by validating the email in the request body", responses = {
            @ApiResponse(responseCode = "200", description = "User activation mail resent", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/public/activate/resend")
    public ResponseEntity<OkDto> resendActivationMail(@RequestBody @Valid EmailDto emailData) {
        userService.resendActivationMail(emailData.email());
        return ResponseEntity.ok(new OkDto("User activation mail resent"));

    }

    @Operation(summary = "List of all users", description = "Lists all users of the database in pages", responses = {
            @ApiResponse(responseCode = "200", description = "Request successfull", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto[].class))),
            @ApiResponse(responseCode = "401", description = "User is not authorized to access this data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedDto.class)))
    }, security = @SecurityRequirement(name = "accessAuth"))
    @GetMapping("/list")
    @RoleValidator
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Stream<UserDto>> getUserList(Pageable pageable) {
        Stream<UserDto> userStream = userService.getUserPage(pageable)
                .map(user -> conversionService.convert(user, UserDto.class));
        return ResponseEntity.ok(userStream);
    }

    @Operation(summary = "Get the logged in user", description = "Return the currently logged in user", responses = {
            @ApiResponse(responseCode = "200", description = "Request successfull", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedDto.class)))
    }, security = @SecurityRequirement(name = "accessAuth"))
    @GetMapping
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUser(userDetails.getUsername());
        UserDto userDto = conversionService.convert(user, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Deactivates the logged in user", description = "Deactivates the currently logged in user", responses = {
            @ApiResponse(responseCode = "200", description = "Deactivating successfull", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkDto.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedDto.class)))
    }, security = @SecurityRequirement(name = "accessAuth"))
    @PutMapping("/deactivate")
    public ResponseEntity<OkDto> deactivateUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deactivateUser(userDetails.getUsername());
        return ResponseEntity.ok(new OkDto("User deactivated successfully"));
    }

    @Operation(summary = "Requests the reset of the password", description = "Sends a reset password mail for the mail in the request body", responses = {
            @ApiResponse(responseCode = "200", description = "Reset mail has been sent", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/public/password/reset/request")
    public ResponseEntity<OkDto> resetPasswordRequest(
            @RequestBody @Valid EmailDto emailData) {
        userService.processResetPasswordRequest(emailData.email());
        return ResponseEntity.ok(new OkDto("Password reset mail has been sent"));
    }

    @Operation(summary = "Reset password", description = "Resets the password by validating the token and the new password", responses = {
            @ApiResponse(responseCode = "200", description = "Password reset successfull", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/public/password/reset")
    public ResponseEntity<OkDto> resetPassword(@RequestBody @Valid PasswordResetDto resetData) {
        userService.resetPassword(resetData.token(), resetData.password(), resetData.rePassword());
        return ResponseEntity.ok(new OkDto("Password reseted successfully"));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> emailExists(EmailExistsException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Email already exists");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> emailDoesNotExists(EmailDoesNotExistException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Email Does Not Exist");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> alreadyEnabled(UserEnabledException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("User Enabled");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> passwordsDontMatch(PasswordsDoNotMatchException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Passwords Do Not Match");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> usernameExists(UsernameExistsException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Username Already Exists");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> unknownToken(UnknownTokenException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Unknown Token");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> tokenExpiryError(TokenExpiredException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Token Expired");
        problem.setDetail(e.getMessage());

        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> weakPassword(NoStrongPasswordException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Weak Password");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> tokenUsed(TokenAlreadyUsedException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Used Token");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> invalidToken(InvalidUserTokenTypeException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid Token");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> passwordIsNull(PasswordIsNullException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("No Password sended");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

}
