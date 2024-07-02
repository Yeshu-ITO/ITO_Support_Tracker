package ITO.Application.ItoSupportTracker.model;


import com.marklogic.client.pojo.annotation.Id;
import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
@Setter
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TicketComment")
public class TicketComment {

    @Id
    private Long commentId;
    @XmlElement
    private Long ticketId;
    @XmlElement
    private String name;
    @XmlElement
    private String message;
    @XmlElement
    private String createDateTime;

    public void setCreateDateTime(LocalDateTime dateTime) {
        this.createDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

}
