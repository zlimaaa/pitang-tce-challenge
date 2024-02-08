package br.com.api.pitang.data.dtos;

import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    private String login;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    private String password;

}
