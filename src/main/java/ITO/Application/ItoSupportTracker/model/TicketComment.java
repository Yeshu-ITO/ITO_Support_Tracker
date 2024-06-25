package ITO.Application.ItoSupportTracker.model;


import com.marklogic.client.pojo.annotation.Id;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Data
@NoArgsConstructor
@XmlRootElement(name = "TicketComment")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketComment {

    @Id
    private Long commentId;
    @XmlElement
    private Long ticketId;
    @XmlElement
    private Long userId;
    @XmlElement
    private String name;
    @XmlElement
    private String message;
    @XmlElement
    private String createDateTime;

}
