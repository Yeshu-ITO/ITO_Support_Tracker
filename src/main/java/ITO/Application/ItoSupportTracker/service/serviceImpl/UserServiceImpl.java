package ITO.Application.ItoSupportTracker.service.serviceImpl;

import ITO.Application.ItoSupportTracker.config.MarklogicConnection;
import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.payload.TicketDto;
import ITO.Application.ItoSupportTracker.repository.UserRepository;
import ITO.Application.ItoSupportTracker.service.UserService;
import ITO.Application.ItoSupportTracker.utility.Constants;
import ITO.Application.ItoSupportTracker.utility.IdGenerator;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private Constants constants;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarklogicConnection marklogicConnection;

    /**
     * @param ticket The Ticket Object
     * @param userId User ID who is creating the Ticket
     * @throws JAXBException
     *
     * Method to Create a new Ticket into the database
     */
    @Override
    public void createTicket(Ticket ticket, Long userId) throws JAXBException {

        // Check if the Category ID , Sub Category ID and User ID is Valid
        XMLDocumentManager docMgr = marklogicConnection.client.newXMLDocumentManager();
        QueryManager queryManager = marklogicConnection.client.newQueryManager();
        StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();

        DocumentDescriptor category = docMgr.exists("/Category/" + constants.CATEGORY_COLLECTION + "/CTR" + ticket.getCategoryId());

        DocumentDescriptor user = docMgr.exists("/User/" + constants.USER_COLLECTION + "/USR" + userId);

        StructuredQueryDefinition structuredQueryDefinition =
                queryBuilder.and(
                        queryBuilder.document("/Category/" + constants.CATEGORY_COLLECTION + "/CTR" + ticket.getCategoryId()),
                        queryBuilder.range(queryBuilder.pathIndex("/Category/SubCategories/subCategory/subCategoryId"),
                                "long",
                                StructuredQueryBuilder.Operator.EQ,
                                ticket.getSubCategoryId())
                );
        SearchHandle resultHandle = queryManager.search(structuredQueryDefinition,new SearchHandle());

        // If any of the ID is Invalid throw error
        if(user == null) throw new ResourceNotFoundException("Invalid User ID");
        if(category == null) throw new ResourceNotFoundException("Invalid Category ID");
        if(Integer.parseInt(ticket.getPriority())-1 > Constants.Priority.values().length) throw new ResourceNotFoundException("Invalid Priority ID");
        if(resultHandle.getMatchResults().length != 1) throw new ResourceNotFoundException("Invalid SubCategory ID");

        DOMHandle contentHandle = new DOMHandle();
        marklogicConnection.docMgr.read(resultHandle.getMatchResults()[0].getUri(),contentHandle);
        Document doc = contentHandle.get();

        NodeList SubCategoryName = doc.getElementsByTagName("subCategoryDesc");
        Element SubCategory = (Element) SubCategoryName.item(Integer.parseInt(ticket.getSubCategoryId())-1);

        NodeList CategoryName = doc.getElementsByTagName("categoryDesc");
        Element Category = (Element) CategoryName.item(0);

        // Set necessary values
        ticket.setTicketId(idGenerator.getNextTicketId());
        ticket.setCreateDateTime(LocalDateTime.now());
        ticket.setLastModifiedDateTime(LocalDateTime.now());
        ticket.setReportedId(userId);
        ticket.setAssigneeId(0L);
        ticket.setSubCategoryId(SubCategory.getTextContent());
        ticket.setCategoryId(Category.getTextContent());
        ticket.setStatus(String.valueOf(Constants.Status.OPEN));
        ticket.setPriority(String.valueOf(Constants.Priority.values()[Integer.parseInt(ticket.getPriority())-1]));
        ticket.setTicketLink("http://localhost:8080/api/user/ticketId?userId=" + userId + "&ticketId=" + ticket.getTicketId());

        //Set the Metadata
        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.TICKET_COLLECTION);
        metadataHandle.getCollections().add(constants.ITO_TRACKER);

        this.userRepository.createTicket(ticket, metadataHandle);
    }

    /**
     * @param userId User ID to get All the Tickets belonging to that user
     * @return List<TicketDto> List of all the Tickets Belonging to the user (Return TicketDtos)
     */
    @Override
    public List<TicketDto> getAllTicketOfUser(Long userId) throws JAXBException {

        // Check if the User ID is Valid
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

    /**
     *
     * @param ticketId Ticket ID to be Fetched
     * @param userId User ID to be Fetched
     * @return Ticket object is returned
     */
    @Override
    public Ticket getTicketDetails(Long ticketId, Long userId) throws JAXBException {

        String uri = "/UserTicket/" + constants.TICKET_COLLECTION + "/TKT" + ticketId + "+USR" + userId;

        // If the Ticket ID and User ID is Correct
        if(marklogicConnection.docMgr.exists(uri) != null){
            return userRepository.findByTicketId(uri);
        }
        else
            throw new ResourceNotFoundException("Invalid Ticket Id : " + ticketId + " and User Id : " + userId);
    }

    /**
     *
     * @param ticketComment TicketComment object to be added
     * @param userId The user ID by whom the ticket is being added
     */
    @Override
    public void addComment(TicketComment ticketComment, Long userId) throws JAXBException {

        // Check if Ticket ID and User ID are Valid
        DocumentDescriptor ticket = marklogicConnection.docMgr.exists("/UserTicket/" + constants.TICKET_COLLECTION + "/TKT" + ticketComment.getTicketId() + "+USR" + userId);

        if(ticket != null){

            ticketComment.setCommentId(idGenerator.getNextCommentId());
            ticketComment.setName("User - " + userRepository.getUserName(userId));
            ticketComment.setTicketCreateDateTime(LocalDateTime.now());

            DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
            metadataHandle.getCollections().add(constants.TICKET_COLLECTION);
            metadataHandle.getCollections().add(constants.ITO_TRACKER);

            userRepository.addCommentToUserTicket(ticketComment, metadataHandle, userId);
        }
        else
            throw new ResourceNotFoundException("NO Ticket Found for the given User-Id :" + userId + " and Ticket-Id : " + ticketComment.getTicketId() + ", Comment cannot be added" );

    }

}
