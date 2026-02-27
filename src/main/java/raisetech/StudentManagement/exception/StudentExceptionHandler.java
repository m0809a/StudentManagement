package raisetech.StudentManagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StudentExceptionHandler {

  @ExceptionHandler(StudentNotFoundException.class)
  public ResponseEntity<String> handleStudentNotFound(StudentNotFoundException e){
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException e){

    List<FieldError> errors = e.getBindingResult().getFieldErrors();

    FieldError error = errors.stream()
        .filter(err -> err.getCode().equals("NotBlank"))
        .findFirst()
        .orElseGet(() -> errors.stream()
            .filter(err -> err.getCode().equals("Email"))
            .findFirst()
            .orElse(errors.get(0)));

    String message = "error : " + error.getDefaultMessage();


    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(message);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handleJsonParseError(
      HttpMessageNotReadableException e) {

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(e.getMessage());
  }

}
