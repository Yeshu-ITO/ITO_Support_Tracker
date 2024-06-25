package ITO.Application.ItoSupportTracker.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketDto {

    private Long ticketId;
    private Long categoryId;
    private Long subCategoryId;
    private Long assigneeId;
    private String subject;
    private String status;
    private String priority;
    private String ticketLink;


}
