package softuni.exam.models.dto;

import softuni.exam.models.entity.Astronomer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "astronomers")
@XmlAccessorType(XmlAccessType.FIELD)
public class AstronomersImportRootDto implements Serializable {

    @XmlElement(name = "astronomer")
    private List<AstronomerImportDto> astronomers;

    public List<AstronomerImportDto> getAstronomers() {
        return astronomers;
    }

    public void setAstronomers(List<AstronomerImportDto> astronomers) {
        this.astronomers = astronomers;
    }
}
