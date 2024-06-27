package ITO.Application.ItoSupportTracker.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketCommentDto {

    private String name;
    private String message;

}
