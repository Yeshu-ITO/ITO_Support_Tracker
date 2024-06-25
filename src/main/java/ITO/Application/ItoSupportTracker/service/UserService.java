package ITO.Application.ItoSupportTracker.service;

import ITO.Application.ItoSupportTracker.config.MarklogicConnection;
import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.payload.TicketDto;
import ITO.Application.ItoSupportTracker.repository.UserRepository;
import ITO.Application.ItoSupportTracker.utility.Constants;
import ITO.Application.ItoSupportTracker.utility.IdGenerator;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private Constants constants;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarklogicConnection marklogicConnection;


    //Working
    public void createTicket(Ticket ticket, Long userId) throws JAXBException {

        XMLDocumentManager docMgr = marklogicConnection.client.newXMLDocumentManager();
        DocumentDescriptor category = docMgr.exists("/Category/" + constants.CATEGORY_COLLECTION + "/CTR" + ticket.getCategoryId());
        DocumentDescriptor subCategory = docMgr.exists("/SubCategory/" + constants.SUBCATEGORY_COLLECTION + "/CTR" + ticket.getCategoryId() +  "+SCTR" + ticket.getSubCategoryId());
        DocumentDescriptor user = docMgr.exists("/User/" + constants.USER_COLLECTION + "/USR" + userId);

        if(category == null || subCategory == null || user == null || Integer.parseInt(ticket.getPriority())-1 > Constants.Priority.values().length)
            throw new ResourceNotFoundException("Invalid CategoryId or SubCategory Id or Priority Id or User Id");

        else {
            // Set necessary values
            ticket.setTicketId(idGenerator.getNextTicketId());
            ticket.setCreateDateTime(String.valueOf(LocalDateTime.now()));
            ticket.setLastModifiedDateTime(String.valueOf(LocalDateTime.now()));
            ticket.setReportedId(userId);
            ticket.setAssigneeId(0L);
            ticket.setStatus(String.valueOf(Constants.Status.OPEN));
            ticket.setPriority(String.valueOf(Constants.Priority.values()[Integer.parseInt(ticket.getPriority())-1]));
            ticket.setTicketLink("/http://localhost:8080/api/user/ticketId?userId=" + userId + "&ticketId=" + ticket.getTicketId());

            //Set the Metadata
            DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
            metadataHandle.getCollections().add(constants.TICKET_COLLECTION);
            metadataHandle.getCollections().add(constants.ITO_TRACKER);

            this.userRepository.createTicket(ticket, metadataHandle);
        }
    }

    //Working
    public List<TicketDto> getAllTicketOfUser(Long userId) throws JAXBException {

        XMLDocumentManager docMgr = marklogicConnection.client.newXMLDocumentManager();
        DocumentDescriptor user = docMgr.exists("/User/" + constants.USER_COLLECTION + "/USR" + userId);

        if(user != null){
            List<TicketDto> allTicketsOfUser = userRepository.getAllTicketOfUser(userId);
            if(!(allTicketsOfUser.isEmpty()))
                    return  allTicketsOfUser;
            else
                throw new ResourceNotFoundException("No Tickets Found");
        }
        else
            throw new ResourceNotFoundException("Invalid User Id");
    }

    //Working
    public Ticket getTicketDetails(Long ticketId, Long userId) throws JAXBException {

        String uri = "/UserTicket/" + constants.TICKET_COLLECTION + "/TKT" + ticketId + "+USR" + userId;

        if(marklogicConnection.docMgr.exists(uri) != null){
            return userRepository.findByTicketId(uri);
        }
        else
            throw new ResourceNotFoundException("Invalid Ticket Id : " + ticketId + " or User Id : " + userId);

    }


    //Working
    public void addComment(TicketComment ticketComment, Long ticketId, Long userId ) throws JAXBException {

        DocumentDescriptor ticket = marklogicConnection.docMgr.exists("/UserTicket/" + constants.TICKET_COLLECTION + "/TKT" + ticketId + "+USR" + userId);

        if(ticket != null){

            ticketComment.setCommentId(idGenerator.getNextCommentId());
            ticketComment.setUserId(userId);
            ticketComment.setTicketId(ticketId);
            ticketComment.setName("User - " + userRepository.getUserName(userId));
            ticketComment.setCreateDateTime(String.valueOf(LocalDateTime.now()));

            DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
            metadataHandle.getCollections().add(constants.COMMENT_COLLECTION);
            metadataHandle.getCollections().add(constants.ITO_TRACKER);

            userRepository.addCommentToUserTicket(ticketComment, metadataHandle);
        }
        else
            throw new ResourceNotFoundException("NO Ticket Found for the given User-Id :" + userId + " and Ticket-Id : " + ticketId + ", Comment cannot be added" );
    }
}
