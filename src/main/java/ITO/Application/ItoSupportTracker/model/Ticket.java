package ITO.Application.ItoSupportTracker.model;

import com.marklogic.client.pojo.annotation.Id;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Getter
@Setter
@Data
@NoArgsConstructor
@XmlRootElement(name = "UserTicket")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ticket {

    @Id
    private Long ticketId;

    @XmlElement(name = "Category")
    @Pattern(regexp = "\\d+", message = "Must contain only digits")
    private String categoryId;

    @XmlElement(name = "SubCategory")
    @Pattern(regexp = "\\d+", message = "Must contain only digits")
    private String subCategoryId;

    @XmlElement
    private Long assigneeId;

    @XmlElement
    private Long reportedId;

    @XmlElement
    @NotEmpty
    private String subject;

    @XmlElement
    @NotEmpty
    private String description;

    @XmlElement
    private String status;

    @XmlElement
    @Pattern(regexp = "\\d+", message = "Must contain only digits")
    private String priority;

    @XmlElement
    private String createDateTime;

    @XmlElement
    private String lastModifiedDateTime;

    @XmlElement
    private String ticketLink;

    @XmlElementWrapper(name = "Comments")
    @XmlElement
    private List<TicketComment> TicketComment;

    public void setCreateDateTime(LocalDateTime dateTime) {
        this.createDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public void setLastModifiedDateTime(LocalDateTime dateTime) {
        this.lastModifiedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

}
