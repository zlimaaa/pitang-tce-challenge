package br.com.api.pitang.data.dtos;

import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import com.fasterxml.jackson.annotation.JsonFormat;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "id", "firstName", "lastName", "birthDate", "email", "login", "phone", "createdAt","lastLogin" })
@JsonIgnoreProperties(value = { "password" }, allowSetters = true)
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    private String firstName;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    private String lastName;

    @NotNull(message = MISSING_FIELDS)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(shape = STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    @Email(message = INVALID_FIELDS)
    private String email;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    private String login;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    private String password;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    @Size(min = 11, max = 11, message = INVALID_FIELDS)
    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(shape = STRING, pattern = "dd/MM/yyyy HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(shape = STRING, pattern = "dd/MM/yyyy HH:mm:ss.SSS")
    private LocalDateTime lastLogin;
}
