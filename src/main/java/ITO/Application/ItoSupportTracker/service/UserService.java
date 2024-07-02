package ITO.Application.ItoSupportTracker.service;


import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.payload.TicketDto;
import jakarta.xml.bind.JAXBException;

import java.util.List;

public interface UserService {

    void createTicket(Ticket ticket, Long userId) throws JAXBException;

    List<TicketDto> getAllTicketOfUser(Long userId) throws JAXBException;

    Ticket getTicketDetails(Long ticketId, Long userId) throws JAXBException;

    void addComment(TicketComment ticketComment, Long userId ) throws JAXBException;

}
