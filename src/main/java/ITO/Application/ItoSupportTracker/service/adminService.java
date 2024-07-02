package ITO.Application.ItoSupportTracker.service;

import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.payload.TicketDto;
import jakarta.xml.bind.JAXBException;

import java.util.Date;
import java.util.List;

public interface adminService {

    List<TicketDto> getAllTickets() throws JAXBException;

    Ticket getTicketDetails(Long ticketId) throws JAXBException;

    void setAssignee(Long ticketId, Long assigneeId) throws JAXBException;

    String changeStatus(Long ticketId, Long userId, Long statusId);

    void addAdminComment(TicketComment ticketComment, Long ticketId) throws JAXBException;

    List<TicketDto> getAssigneeTickets(Long assigneeId) throws JAXBException;

    List<TicketDto> getTicketsByDate(String date) throws JAXBException;

}
