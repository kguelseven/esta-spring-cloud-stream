package ch.sbb.esta.scs.configuration.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {

    @NotNull(message = "book Id is mandatory")
    private Long bookId;
    @NotBlank(message = "Request Id is mandatory")
    private String requestId;

}
