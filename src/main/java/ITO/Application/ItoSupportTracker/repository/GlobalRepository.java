package ITO.Application.ItoSupportTracker.repository;

import ITO.Application.ItoSupportTracker.config.MarklogicConnection;
import ITO.Application.ItoSupportTracker.model.AdminTeam;
import ITO.Application.ItoSupportTracker.model.Category;
import ITO.Application.ItoSupportTracker.model.User;
import ITO.Application.ItoSupportTracker.utility.Constants;
import ITO.Application.ItoSupportTracker.utility.IdGenerator;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.StringWriter;

@Repository
public class GlobalRepository {

    @Autowired
    private MarklogicConnection marklogicConnection;

    @Autowired
    private Constants constants;

    @Autowired
    private IdGenerator idGenerator;


    public void addAdmin(AdminTeam adminTeam) throws JAXBException {

        adminTeam.setAdminId(idGenerator.getNextAdminId());

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.ITO_TRACKER);
        metadataHandle.getCollections().add(constants.ADMIN_COLLECTION);

        JAXBContext context = JAXBContext.newInstance(AdminTeam.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(adminTeam,writer);

        marklogicConnection.docMgr.write(
                "/Admin/" + constants.ADMIN_COLLECTION + "/ADM" + adminTeam.getAdminId(),
                metadataHandle,
                new StringHandle(writer.toString()));

    }


    public void addUser(User user) throws JAXBException {

        user.setUserId(idGenerator.getNextUserId());

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.ITO_TRACKER);
        metadataHandle.getCollections().add(constants.USER_COLLECTION);

        JAXBContext context = JAXBContext.newInstance(User.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(user,writer);

        marklogicConnection.docMgr.write(
                "/User/" + constants.USER_COLLECTION + "/USR" + user.getUserId(),
                metadataHandle,
                new StringHandle(writer.toString()));

    }

    public void addCategory(Category category) throws JAXBException {

        category.setCategoryId(idGenerator.getNextCategoryId());

        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().add(constants.ITO_TRACKER);
        metadataHandle.getCollections().add(constants.CATEGORY_COLLECTION);

        JAXBContext context = JAXBContext.newInstance(Category.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(category,writer);

        marklogicConnection.docMgr.write(
                "/Category/" + constants.CATEGORY_COLLECTION + "/CTR" + category.getCategoryId(),
                metadataHandle,
                new StringHandle(writer.toString()));

    }

//    public void addSubCategory(SubCategory subCategory,Long categoryId, Long subCategoryId) throws JAXBException {
//
//        subCategory.setSubCategoryId(subCategoryId);
//        subCategory.setCategoryId(categoryId);
//
//        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
//        metadataHandle.getCollections().add(constants.ITO_TRACKER);
//        metadataHandle.getCollections().add(constants.SUBCATEGORY_COLLECTION);
//
//        JAXBContext context = JAXBContext.newInstance(SubCategory.class);
//        Marshaller marshaller = context.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
//
//        StringWriter writer = new StringWriter();
//        marshaller.marshal(subCategory,writer);
//
//        marklogicConnection.docMgr.write(
//                "/SubCategory/" + constants.SUBCATEGORY_COLLECTION + "/CTR" + subCategory.getCategoryId() +  "+SCTR" + subCategory.getSubCategoryId(),
//                metadataHandle,
//                new StringHandle(writer.toString()));
//
//    }



}
