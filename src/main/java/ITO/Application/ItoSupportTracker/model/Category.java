package ITO.Application.ItoSupportTracker.model;


import com.marklogic.client.pojo.annotation.Id;
import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Category")
@XmlAccessorType(XmlAccessType.FIELD)
public class Category {

    @Id
    private Long categoryId;
    @XmlElement
    private String categoryDesc;
    @XmlElementWrapper(name = "SubCategories")
    @XmlElement
    private List<SubCategory> subCategory;
}
