package Onlinestorerestapi.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException {
  private String message;

    public NotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
