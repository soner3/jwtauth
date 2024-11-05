package net.sonerapp.jwtauth.interfaces.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import net.sonerapp.jwtauth.core.exceptions.DeleteEntityException;
import net.sonerapp.jwtauth.core.exceptions.IllegalUuidException;
import net.sonerapp.jwtauth.core.exceptions.InvalidOwnerException;
import net.sonerapp.jwtauth.core.exceptions.NoEntityDeletedException;
import net.sonerapp.jwtauth.core.exceptions.OutOfBoundsException;
import net.sonerapp.jwtauth.infrastructure.exceptions.AccessDeniedException;
import net.sonerapp.jwtauth.infrastructure.exceptions.JwtClaimEmptyException;
import net.sonerapp.jwtauth.infrastructure.exceptions.JwtExpiredException;

@ControllerAdvice("net.sonerapp.jwtauth.interfaces")
public class ApiControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> validateDto(MethodArgumentNotValidException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Field Error");
        Map<String, Object> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        });
        problem.setDetail("Validation failed for the following fields:");
        problem.setProperty("errors", errors);
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> entityNotFound(OutOfBoundsException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Entity Not Found");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> deleteEntityError(DeleteEntityException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Delete Entity Error");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> inavlidJwt(MalformedJwtException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Malformed JWT");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> deleteEntityError(JwtExpiredException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("JWT Expired");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> deleteEntityError(UnsupportedJwtException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Unsupported JWT");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> deleteEntityError(JwtClaimEmptyException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("JWT Claim Empty");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> wrongData(DataIntegrityViolationException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("SQL-Query Failed");
        problem.setDetail("SQL-Query could not be successfully processed with the Data");
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> noBody(HttpMessageNotReadableException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("No Request Body Found");
        problem.setDetail("No readable body was found in the request.");
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> wrongSignature(SignatureException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Invalid Signature");
        problem.setDetail("The token is not signed with the signature used in the system.");
        return ResponseEntity.of(problem).build();

    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> usernameNotFound(UsernameNotFoundException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("User Not Found");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> invalidProperties(PropertyReferenceException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid Propertys");
        problem.setDetail("Invalid request properties has been sent ti this ressource");
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> illegalUuid(IllegalUuidException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid UUID");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> entityDeletionFailed(NoEntityDeletedException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Delete Failed");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> convertetToNull(ConvertetToNullException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Conversion Failed");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> accessDenied(AccessDeniedException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Access Denied");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> invalidOwner(InvalidOwnerException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Invalid Owner");
        problem.setDetail(e.getMessage());
        return ResponseEntity.of(problem).build();
    }

}
