package ITO.Application.ItoSupportTracker.payload;


import ITO.Application.ItoSupportTracker.model.Ticket;
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
    public List<Communication> communications;

}
