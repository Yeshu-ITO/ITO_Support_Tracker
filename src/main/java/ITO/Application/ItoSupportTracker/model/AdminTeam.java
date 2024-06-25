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
@XmlRootElement(name = "Admin")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdminTeam {

    @Id
    private Long adminId;
    @XmlElement
    private String adminName;
    @XmlElement
    private String adminEmail;

}
