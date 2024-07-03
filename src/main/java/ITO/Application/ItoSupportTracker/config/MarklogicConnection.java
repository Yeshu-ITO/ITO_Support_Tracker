package ITO.Application.ItoSupportTracker.config;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class MarklogicConnection {

    public final DatabaseClient client;
    public final XMLDocumentManager docMgr;

    public MarklogicConnection() {
            client = DatabaseClientFactory.newClient("localhost", 8000, "JavaAPI",
                    new DatabaseClientFactory.DigestAuthContext("admin", "admin"));
            docMgr = client.newXMLDocumentManager();
    }

}
