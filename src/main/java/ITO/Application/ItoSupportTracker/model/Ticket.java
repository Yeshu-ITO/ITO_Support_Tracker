package ITO.Application.ItoSupportTracker.model;

import com.marklogic.client.pojo.annotation.Id;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.*;
import lombok.*;


import java.time.LocalDateTime;


@Getter
@Setter
@Data
@NoArgsConstructor
@XmlRootElement(name = "UserTicket")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ticket {

    @Id
    private Long ticketId;

    @XmlElement
    private Long categoryId;

    @XmlElement
    private Long subCategoryId;

    @XmlElement
    private Long assigneeId;

    @XmlElement
    private Long reportedId;

    @XmlElement
    private String subject;

    @XmlElement
    private String description;

    @XmlElement
    private String status;

    @XmlElement
    private String priority;

    @XmlElement
    private String createDateTime;

    @XmlElement
    private String lastModifiedDateTime;

    @XmlElement
    private String ticketLink;

}
