package ITO.Application.ItoSupportTracker.model;


import com.marklogic.client.pojo.annotation.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "\\d+", message = "Must contain only digits")
    private String ticketId;
    @XmlElement
    private String name;
    @XmlElement
    @NotEmpty
    private String message;
    @XmlElement
    private String ticketCreateDateTime;

    public void setTicketCreateDateTime(LocalDateTime dateTime) {
        this.ticketCreateDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

}
