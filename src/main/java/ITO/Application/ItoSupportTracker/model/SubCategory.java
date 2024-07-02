package ITO.Application.ItoSupportTracker.model;


import com.marklogic.client.pojo.annotation.Id;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "SubCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubCategory {

    @Id
    private Long subCategoryId;
//    @XmlElement
//    private Long categoryId;
    @XmlElement
    private String subCategoryDesc;
}
