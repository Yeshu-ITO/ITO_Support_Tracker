package ITO.Application.ItoSupportTracker.repository;


import ITO.Application.ItoSupportTracker.config.MarklogicConnection;
import ITO.Application.ItoSupportTracker.utility.Constants;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class adminRepository {

    @Autowired
    private MarklogicConnection marklogicConnection;

    @Autowired
    private Constants constants;


    public SearchHandle getAllTickets() {

        QueryManager queryManager = marklogicConnection.client.newQueryManager();
        StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();

        StructuredQueryDefinition structuredQueryDefinition =
                queryBuilder.and(
                        queryBuilder.collection(constants.TICKET_COLLECTION),
                        queryBuilder.collection(constants.ITO_TRACKER)
                );
        return queryManager.search(structuredQueryDefinition,new SearchHandle());
    }


    public SearchHandle getTicketDetails(Long ticketId, String path) throws JAXBException {

        QueryManager queryManager = marklogicConnection.client.newQueryManager();
        StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();

        StructuredQueryDefinition structuredQueryDefinition =
                queryBuilder.and(
                        queryBuilder.collection(constants.TICKET_COLLECTION),
                        queryBuilder.collection(constants.ITO_TRACKER),
                        queryBuilder.range(queryBuilder.pathIndex(path), "long", StructuredQueryBuilder.Operator.EQ, ticketId)
                );

        return queryManager.search(structuredQueryDefinition,new SearchHandle());
    }


    public void updateDocument(String uri, DocumentMetadataHandle metadataHandle, DOMHandle contentHandle){
        marklogicConnection.docMgr.write(uri,metadataHandle,contentHandle);
    }


    public void insertDocument(String uri, DocumentMetadataHandle metadataHandle, StringHandle data) throws JAXBException {
            marklogicConnection.docMgr.write(uri,metadataHandle,data);
    }


    public SearchHandle getCommentDetails(Long ticketId,String path) throws JAXBException {

        QueryManager queryManager = marklogicConnection.client.newQueryManager();
        StructuredQueryBuilder queryBuilder = queryManager.newStructuredQueryBuilder();

        StructuredQueryDefinition structuredQueryDefinition =
                queryBuilder.and(
                        queryBuilder.collection(constants.COMMENT_COLLECTION),
                        queryBuilder.collection(constants.ITO_TRACKER),
                        queryBuilder.range(queryBuilder.pathIndex(path), "long", StructuredQueryBuilder.Operator.EQ, ticketId)
                );

        return queryManager.search(structuredQueryDefinition,new SearchHandle());
    }
}
