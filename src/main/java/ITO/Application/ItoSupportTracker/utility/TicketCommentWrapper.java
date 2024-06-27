package ITO.Application.ItoSupportTracker.utility;


import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.payload.TicketCommentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
public class TicketCommentWrapper {

    public Ticket ticket;
    public List<TicketCommentDto> ticketCommentDtos;

}
