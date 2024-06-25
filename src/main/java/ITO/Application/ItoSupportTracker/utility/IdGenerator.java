package ITO.Application.ItoSupportTracker.utility;

import ITO.Application.ItoSupportTracker.config.MarklogicConnection;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    @Autowired
    private MarklogicConnection marklogicConnection;

    @Autowired
    private Constants constants;

    public synchronized Long  getNextTicketId() {
        DocumentManager docMgr = marklogicConnection.client.newDocumentManager();

        DocumentDescriptor documentDescriptor = docMgr.exists(constants.TICKET_ID_COUNTER);

        long ticketId = 1;

        if (documentDescriptor != null) {
            StringHandle handle = new StringHandle();
            docMgr.read(constants.TICKET_ID_COUNTER, handle);
            ticketId = Long.parseLong(handle.get());
        }

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.ITO_TRACKER);
        metadataHandle.getCollections().add(constants.COUNTER);

        docMgr.write(constants.TICKET_ID_COUNTER, metadataHandle, new StringHandle(String.valueOf(ticketId + 1)));

        return ticketId;

    }

    public Long getNextAdminId() {

        DocumentManager docMgr = marklogicConnection.client.newDocumentManager();

        DocumentDescriptor documentDescriptor = docMgr.exists(constants.ADMIN_ID_COUNTER);

        long adminId = 1;

        if (documentDescriptor != null) {
            StringHandle handle = new StringHandle();
            docMgr.read(constants.ADMIN_ID_COUNTER, handle);
            adminId = Long.parseLong(handle.get());
        }

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.ITO_TRACKER);
        metadataHandle.getCollections().add(constants.COUNTER);


        docMgr.write(constants.ADMIN_ID_COUNTER, metadataHandle, new StringHandle(String.valueOf(adminId + 1)));

        return adminId;
    }

    public Long getNextUserId() {

        DocumentManager docMgr = marklogicConnection.client.newDocumentManager();

        DocumentDescriptor documentDescriptor = docMgr.exists(constants.USER_ID_COUNTER);

        long userID = 1;

        if (documentDescriptor != null) {
            StringHandle handle = new StringHandle();
            docMgr.read(constants.USER_ID_COUNTER, handle);
            userID = Long.parseLong(handle.get());
        }

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.ITO_TRACKER);
        metadataHandle.getCollections().add(constants.COUNTER);


        docMgr.write(constants.USER_ID_COUNTER, metadataHandle, new StringHandle(String.valueOf(userID + 1)));

        return userID;
    }

    public Long getNextCommentId() {
        DocumentManager docMgr = marklogicConnection.client.newDocumentManager();

        DocumentDescriptor documentDescriptor = docMgr.exists(constants.COMMENT_ID_COUNTER);

        long commentId = 1;

        if (documentDescriptor != null) {
            StringHandle handle = new StringHandle();
            docMgr.read(constants.COMMENT_ID_COUNTER, handle);
            commentId = Long.parseLong(handle.get());
        }

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.ITO_TRACKER);
        metadataHandle.getCollections().add(constants.COUNTER);


        docMgr.write(constants.COMMENT_ID_COUNTER, metadataHandle, new StringHandle(String.valueOf(commentId + 1)));

        return commentId;

    }

    public Long getNextCategoryId() {

        DocumentManager docMgr = marklogicConnection.client.newDocumentManager();

        DocumentDescriptor documentDescriptor = docMgr.exists(constants.CATEGORY_ID_COUNTER);

        long categoryId = 1;

        if (documentDescriptor != null) {
            StringHandle handle = new StringHandle();
            docMgr.read(constants.CATEGORY_ID_COUNTER, handle);
            categoryId = Long.parseLong(handle.get());
        }

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.ITO_TRACKER);
        metadataHandle.getCollections().add(constants.COUNTER);


        docMgr.write(constants.CATEGORY_ID_COUNTER, metadataHandle, new StringHandle(String.valueOf(categoryId + 1)));

        return categoryId;

    }

//    public Long getNextSubCategoryId() {
//
//        DocumentManager docMgr = marklogicConnection.client.newDocumentManager();
//
//        DocumentDescriptor documentDescriptor = docMgr.exists(constants.SUBCATEGORY_ID_COUNTER);
//
//        long subcategoryId = 1;
//
//        if (documentDescriptor != null) {
//            StringHandle handle = new StringHandle();
//            docMgr.read(constants.SUBCATEGORY_ID_COUNTER, handle);
//            subcategoryId = Long.parseLong(handle.get());
//        }
//
//        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
//        metadataHandle.getCollections().add(constants.ITO_TRACKER);
//        metadataHandle.getCollections().add(constants.COUNTER);
//
//
//        docMgr.write(constants.SUBCATEGORY_ID_COUNTER, metadataHandle, new StringHandle(String.valueOf(subcategoryId + 1)));
//
//        return subcategoryId;
//
//    }
}