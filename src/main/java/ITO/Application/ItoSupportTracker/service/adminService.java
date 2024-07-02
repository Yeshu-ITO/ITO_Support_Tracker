package ITO.Application.ItoSupportTracker.service;


import ITO.Application.ItoSupportTracker.config.MarklogicConnection;
import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.payload.TicketCommentDto;
import ITO.Application.ItoSupportTracker.utility.TicketCommentWrapper;
import ITO.Application.ItoSupportTracker.payload.TicketDto;
import ITO.Application.ItoSupportTracker.repository.adminRepository;
import ITO.Application.ItoSupportTracker.utility.Constants;
import ITO.Application.ItoSupportTracker.utility.IdGenerator;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class adminService {

    @Autowired
    private MarklogicConnection marklogicConnection;

    @Autowired
    private Constants constants;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TicketCommentWrapper ticketCommentWrapper;

    @Autowired
    private adminRepository adminRepository;

    @Autowired
    private IdGenerator idGenerator;

    // Method to get All the Tickets
    public List<TicketDto> getAllTickets() throws JAXBException {

        SearchHandle resultHandle = adminRepository.getAllTickets();

        List<Ticket> allTicketsOfUser = new ArrayList<>();

        for (MatchDocumentSummary result : resultHandle.getMatchResults()) {

            StringHandle contentHandle = new StringHandle();
            marklogicConnection.docMgr.read(result.getUri(), contentHandle);

            JAXBContext context = JAXBContext.newInstance(Ticket.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Ticket ticket = (Ticket) unmarshaller.unmarshal(new StringReader(contentHandle.get()));
            allTicketsOfUser.add(ticket);
        }

        if(!allTicketsOfUser.isEmpty()){
            return allTicketsOfUser.stream().map((ticket -> modelMapper.map(ticket,TicketDto.class))).toList();
        }
        else
            throw new ResourceNotFoundException("NO Tickets found");
    }

    // Method to get the Details of a Ticket and all its Communication
    public TicketCommentWrapper getTicketDetails(Long ticketId) throws JAXBException {

        //Get Ticket Details
        String Ticketpath = "/UserTicket/ticketId";
        SearchHandle ticketDetails = adminRepository.getTicketDetails(ticketId,Ticketpath);
        MatchDocumentSummary[] results = ticketDetails.getMatchResults();
        if(results.length != 1)
            throw new ResourceNotFoundException("Invalid Ticket Id");

        StringHandle contentHandle = new StringHandle();
        marklogicConnection.docMgr.read(results[0].getUri(),contentHandle);
        JAXBContext context = JAXBContext.newInstance(Ticket.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(contentHandle.get());
        Ticket ticket = (Ticket) unmarshaller.unmarshal(reader);


        //Get All Comments
        String commentPath = "/TicketComment/ticketId";
        SearchHandle comments = adminRepository.getCommentDetails(ticketId,commentPath);
        List<TicketComment> ticketComments = new ArrayList<>();
        for (MatchDocumentSummary result : comments.getMatchResults()) {

            StringHandle commentHandle = new StringHandle();
            marklogicConnection.docMgr.read(result.getUri(), commentHandle);

            JAXBContext context1 = JAXBContext.newInstance(TicketComment.class);
            Unmarshaller unmarshaller1 = context1.createUnmarshaller();
            StringReader reader1 = new StringReader( commentHandle.get());
            TicketComment ticketComment = (TicketComment) unmarshaller1.unmarshal(reader1);
            ticketComments.add(ticketComment);
        }

        List<TicketCommentDto> TicketCommentDto = ticketComments.stream().map(ticketComment -> modelMapper.map(ticketComment, TicketCommentDto.class)).toList();

        ticketCommentWrapper.setTicket(ticket);
        ticketCommentWrapper.setTicketCommentDtos(TicketCommentDto);

        return  ticketCommentWrapper;
    }

    // Method to set an Assignee to a Ticket
    public void setAssignee(Long ticketId, Long assigneeId) throws JAXBException {

        // Check if the Assignee ID is valid
        DocumentDescriptor admin  = marklogicConnection.docMgr.exists("/Admin/"+ constants.ADMIN_COLLECTION + "/ADM" + assigneeId);

        if(admin != null){
            String path = "/UserTicket/ticketId";
            SearchHandle searchHandle = adminRepository.getTicketDetails(ticketId,path);
            MatchDocumentSummary[] results = searchHandle.getMatchResults();
            // If the Ticket ID is Invalid
            if(results.length != 1)
                throw new ResourceNotFoundException("Invalid Ticket Id");

            DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

            DOMHandle contentHandle = new DOMHandle();
            marklogicConnection.docMgr.read(results[0].getUri(),contentHandle);
            Document doc = contentHandle.get();

            // Update the Assignee Node
            NodeList assigneeNode = doc.getElementsByTagName("assigneeId");
            Element assigneElement = (Element) assigneeNode.item(0);
            assigneElement.setTextContent(String.valueOf(assigneeId));

            //Update the LastModifiedDateTime
            NodeList last_modified_dataTime = doc.getElementsByTagName("lastModifiedDateTime");
            Element last_modified_dataTime_element = (Element) last_modified_dataTime.item(0);
            last_modified_dataTime_element.setTextContent(String.valueOf(LocalDateTime.now()));

            contentHandle.set(doc);

            metadataHandle.withCollections(constants.TICKET_COLLECTION);
            metadataHandle.withCollections(constants.ITO_TRACKER);

            adminRepository.updateDocument(results[0].getUri(),metadataHandle,contentHandle);
        }
        else
            throw new ResourceNotFoundException("Invalid Admin Id");
    }

    // Method to change the Status of the Ticket
    public String changeStatus(Long ticketId, Long userId, Long statusId){

        String uri = "/UserTicket/"+ constants.TICKET_COLLECTION + "/TKT" + ticketId + "+USR" + userId;

        // Check if the Ticket ID and User ID is Valid
        if(marklogicConnection.docMgr.exists(uri) == null)
            throw new ResourceNotFoundException("Invalid Ticket Id or User Id");

        // Check if the Status ID is Valid
        if(statusId-1 > Constants.Status.values().length)
            throw new ResourceNotFoundException("Invalid Status Id");

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        DOMHandle contentHandle = new DOMHandle();

        marklogicConnection.docMgr.read(uri,contentHandle);
        Document doc = contentHandle.get();

        // Update the Status
        NodeList statusNode = doc.getElementsByTagName("status");
        Element statusElement = (Element) statusNode.item(0);
        String oldStatus = statusElement.getTextContent();
        statusElement.setTextContent(String.valueOf(Constants.Status.values()[Math.toIntExact(statusId)-1]));

        // Update the LastModifiedDateTime
        NodeList last_modified_dataTime = doc.getElementsByTagName("lastModifiedDateTime");
        Element last_modified_dataTime_element = (Element) last_modified_dataTime.item(0);
        last_modified_dataTime_element.setTextContent(String.valueOf(LocalDateTime.now()));

        contentHandle.set(doc);

        metadataHandle.withCollections(constants.TICKET_COLLECTION);
        metadataHandle.withCollections(constants.ITO_TRACKER);

        adminRepository.updateDocument(uri,metadataHandle,contentHandle);

        return oldStatus;
    }

    // Method to Add Comment By Admin
    public void addAdminComment(TicketComment ticketComment, Long adminId, Long ticketId) throws JAXBException {

        String uri = "/Admin/" + constants.ADMIN_COLLECTION + "/ADM" +  adminId;

        // If the Admin ID is valid
        if(marklogicConnection.docMgr.exists(uri) != null){

            //Get Admin Name
            DOMHandle contentHandle = new DOMHandle();
            marklogicConnection.docMgr.read(uri,contentHandle);
            Document doc = contentHandle.get();
            NodeList statusNode = doc.getElementsByTagName("adminName");
            Element statusElement = (Element) statusNode.item(0);
            String adminName = statusElement.getTextContent();

            //Check if Ticket Exists
            String path = "/UserTicket/ticketId";
            SearchHandle searchHandle = adminRepository.getTicketDetails(ticketId,path);
            MatchDocumentSummary[] results = searchHandle.getMatchResults();
            if(results.length != 1)
                throw new ResourceNotFoundException("Invalid Ticket Id");

            //Set Necessary Values
            ticketComment.setCommentId(idGenerator.getNextCommentId());
//            ticketComment.setUserId(adminId);
            ticketComment.setTicketId(ticketId);
            ticketComment.setCreateDateTime(LocalDateTime.now());
            ticketComment.setName("Admin - " + adminName);

            DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
            metadataHandle.getCollections().add(constants.COMMENT_COLLECTION);
            metadataHandle.getCollections().add(constants.ITO_TRACKER);

            JAXBContext context = JAXBContext.newInstance(TicketComment.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(ticketComment,writer);

            String newUri = "/TicketComment/" + constants.COMMENT_COLLECTION + "/CMT" + ticketComment.getCommentId() +"+TKT" + ticketId;

            adminRepository.insertDocument(newUri,metadataHandle,new StringHandle(writer.toString()));
        }
        else {
            throw new ResourceNotFoundException("Invalid Admin Id");
        }
}

}
