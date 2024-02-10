package br.com.api.pitang.data.dtos;

import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
@JsonPropertyOrder({ "id", "year", "licensePlate", "model", "color" })
public class CarDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Min(value = 1885, message = INVALID_FIELDS)
    @NotNull(message = MISSING_FIELDS)
    private Integer year;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    @Size(min = 7, max = 7, message = INVALID_FIELDS)
    private String licensePlate;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    private String model;

    @NotNull(message = MISSING_FIELDS)
    @NotEmpty(message = MISSING_FIELDS)
    private String color;

}
