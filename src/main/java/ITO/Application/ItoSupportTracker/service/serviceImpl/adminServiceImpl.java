package ITO.Application.ItoSupportTracker.service.serviceImpl;

import ITO.Application.ItoSupportTracker.config.MarklogicConnection;
import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.payload.TicketDto;
import ITO.Application.ItoSupportTracker.service.adminService;
import ITO.Application.ItoSupportTracker.utility.Constants;
import ITO.Application.ItoSupportTracker.utility.IdGenerator;
import ITO.Application.ItoSupportTracker.utility.TicketCommentWrapper;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
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
public class adminServiceImpl implements adminService {

    @Autowired
    private MarklogicConnection marklogicConnection;

    @Autowired
    private Constants constants;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TicketCommentWrapper ticketCommentWrapper;

    @Autowired
    private ITO.Application.ItoSupportTracker.repository.adminRepository adminRepository;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * @return
     * @throws JAXBException
     */
    @Override
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

    /**
     * @param ticketId
     * @return
     * @throws JAXBException
     */
    @Override
    public Ticket getTicketDetails(Long ticketId) throws JAXBException {
        //Get Ticket Details
        String TicketPath = "/UserTicket/ticketId";
        SearchHandle ticketDetails = adminRepository.getTicketDetails(ticketId,TicketPath);
        MatchDocumentSummary[] results = ticketDetails.getMatchResults();
        if(results.length != 1)
            throw new ResourceNotFoundException("Invalid Ticket Id");

        StringHandle contentHandle = new StringHandle();
        marklogicConnection.docMgr.read(results[0].getUri(),contentHandle);
        JAXBContext context = JAXBContext.newInstance(Ticket.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(contentHandle.get());
        return  (Ticket) unmarshaller.unmarshal(reader);
    }

    /**
     * @param ticketId
     * @param assigneeId
     * @throws JAXBException
     */
    @Override
    public void setAssignee(Long ticketId, Long assigneeId) throws JAXBException {
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

    /**
     * @param ticketId
     * @param userId
     * @param statusId
     * @return
     */
    @Override
    public String changeStatus(Long ticketId, Long userId, Long statusId) {
        String uri = "/UserTicket/"+ constants.TICKET_COLLECTION + "/TKT" + ticketId + "+USR" + userId;

        // Check if the Ticket ID and User ID is Valid
        if(marklogicConnection.docMgr.exists(uri) == null)
            throw new ResourceNotFoundException("Invalid Ticket Id and User Id");

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

    /**
     * @param ticketComment
     * @param adminId
     * @throws JAXBException
     */
    @Override
    public void addAdminComment(TicketComment ticketComment, Long adminId) throws JAXBException {
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
            SearchHandle searchHandle = adminRepository.getTicketDetails(ticketComment.getCommentId(),path);
            MatchDocumentSummary[] results = searchHandle.getMatchResults();
            if(results.length != 1)
                throw new ResourceNotFoundException("Invalid Ticket Id");

            //Set Necessary Values
            ticketComment.setCommentId(idGenerator.getNextCommentId());
            ticketComment.setTicketCreateDateTime(LocalDateTime.now());
            ticketComment.setName("Admin - " + adminName);

            DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
            metadataHandle.getCollections().add(constants.TICKET_COLLECTION);
            metadataHandle.getCollections().add(constants.ITO_TRACKER);

            JAXBContext context = JAXBContext.newInstance(TicketComment.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(ticketComment,writer);

            // Adding to the existing Ticket
            StringHandle contentHandle1 = new StringHandle();
            marklogicConnection.docMgr.read(
                    results[0].getUri(),
                    contentHandle1);

            String existingXml = contentHandle1.get();
            int index = existingXml.indexOf("</Comments>");
            StringHandle newXml;
            if(index != -1){
                StringBuilder sb = new StringBuilder(existingXml);
                sb.insert(index,writer.toString().substring(writer.toString().indexOf("<TicketComment>")));
                newXml = new StringHandle(sb.toString());
            }
            else{
                int newIndex = existingXml.indexOf("</UserTicket>");
                StringBuilder sb = new StringBuilder(existingXml);
                sb.insert(newIndex,"<Comments>" + writer.toString().substring(writer.toString().indexOf("<TicketComment>")) + "</Comments>");
                newXml = new StringHandle(sb.toString());
            }

            adminRepository.insertDocument(results[0].getUri(),metadataHandle,newXml);
        }
        else {
            throw new ResourceNotFoundException("Invalid Admin Id");
        }
    }

    /**
     * @param assigneeId
     * @return
     * @throws JAXBException
     */
    @Override
    public List<TicketDto> getAssigneeTickets(Long assigneeId) throws JAXBException {

        List<TicketDto> allTicketsOfAssignee = new ArrayList<>();

        QueryManager queryManager = marklogicConnection.client.newQueryManager();
        StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();

        StructuredQueryDefinition structuredQueryDefinition =
                queryBuilder.and(
                        queryBuilder.collection(constants.TICKET_COLLECTION),
                        queryBuilder.range(queryBuilder.pathIndex("/UserTicket/assigneeId"),"long", StructuredQueryBuilder.Operator.EQ,assigneeId)
                );

        SearchHandle resultHandle = queryManager.search(structuredQueryDefinition,new SearchHandle());

        for (MatchDocumentSummary result : resultHandle.getMatchResults()) {

            StringHandle contentHandle = new StringHandle();

            marklogicConnection.docMgr.read(result.getUri(), contentHandle);

            // Unmarshal XML content to Ticket object
            JAXBContext context = JAXBContext.newInstance(Ticket.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(contentHandle.get());
            Ticket ticket = (Ticket) unmarshaller.unmarshal(reader);

            // Map Ticket object to TicketDto using ModelMapper
            TicketDto ticketDto = this.modelMapper.map(ticket, TicketDto.class);
            allTicketsOfAssignee.add(ticketDto);
        }
        return allTicketsOfAssignee;
    }
}
