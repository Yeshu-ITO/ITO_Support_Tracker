package ITO.Application.ItoSupportTracker.repository;

import ITO.Application.ItoSupportTracker.config.MarklogicConnection;
import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.utility.Constants;
import ITO.Application.ItoSupportTracker.payload.TicketDto;
import com.marklogic.client.io.*;

import java.io.StringReader;
import java.io.StringWriter;

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
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private MarklogicConnection marklogicConnection;

    @Autowired
    private Constants constants;

    @Autowired
    private ModelMapper modelMapper;

    public void createTicket(Ticket ticket, DocumentMetadataHandle metadataHandle) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(Ticket.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(ticket,writer);

        marklogicConnection.docMgr.write(
                "/UserTicket/" + constants.TICKET_COLLECTION + "/TKT" + ticket.getTicketId() + "+USR" + ticket.getReportedId(),
                metadataHandle,
                new StringHandle(writer.toString()));
    }


    public List<TicketDto> getAllTicketOfUser(Long userId) throws JAXBException {

        List<TicketDto> allTicketsOfUser = new ArrayList<>();

        QueryManager queryManager = marklogicConnection.client.newQueryManager();
        StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();

        StructuredQueryDefinition structuredQueryDefinition =
                queryBuilder.and(
                        queryBuilder.collection(constants.TICKET_COLLECTION),
                        queryBuilder.range(queryBuilder.pathIndex("/UserTicket/reportedId"),"long", StructuredQueryBuilder.Operator.EQ,userId)
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
            allTicketsOfUser.add(ticketDto);
        }

        return allTicketsOfUser;
    }


    public Ticket findByTicketId(String uri) throws JAXBException {

        StringHandle contentHandle = new StringHandle();

        marklogicConnection.docMgr.read(uri, contentHandle);

        // Unmarshal XML content to Ticket object
        JAXBContext context = JAXBContext.newInstance(Ticket.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(contentHandle.get());
        return (Ticket) unmarshaller.unmarshal(reader);
    }


    public void addCommentToUserTicket(TicketComment ticketComment, DocumentMetadataHandle metadataHandle, Long userId) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(TicketComment.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(ticketComment,writer);

        StringHandle contentHandle = new StringHandle();
        marklogicConnection.docMgr.read(
                "/UserTicket/" + constants.TICKET_COLLECTION + "/TKT" + ticketComment.getTicketId() + "+USR" + userId,
                contentHandle);

        String existingXml = contentHandle.get();
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

        marklogicConnection.docMgr.write(
                "/UserTicket/" + constants.TICKET_COLLECTION + "/TKT" + ticketComment.getTicketId() + "+USR" + userId,
                metadataHandle,
                newXml);

    }

    public String getUserName(Long userId) throws JAXBException {

        QueryManager queryManager = marklogicConnection.client.newQueryManager();
        StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();

        StructuredQueryDefinition structuredQueryDefinition =
                queryBuilder.and(
                        queryBuilder.collection(constants.USER_COLLECTION),
                        queryBuilder.range(queryBuilder.pathIndex("/User/userId"),"long", StructuredQueryBuilder.Operator.EQ,userId)
                );

        SearchHandle resultHandle = queryManager.search(structuredQueryDefinition,new SearchHandle());

        String uri = resultHandle.getMatchResults()[0].getUri();

        DOMHandle contentHandle = new DOMHandle();
        marklogicConnection.docMgr.read(uri,contentHandle);
        Document doc = contentHandle.get();

        NodeList userNameNode = doc.getElementsByTagName("userName");
        Element userName = (Element) userNameNode.item(0);
        return userName.getTextContent();

    }
}