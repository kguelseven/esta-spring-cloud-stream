package ch.sbb.esta.scs.book;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @NotNull
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @Positive
    private long pagesCount;

}
