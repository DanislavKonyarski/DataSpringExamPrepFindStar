package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.AstronomersImportRootDto;
import softuni.exam.models.entity.Astronomer;
import softuni.exam.models.entity.Star;
import softuni.exam.repository.AstronomerRepository;
import softuni.exam.repository.StarRepository;
import softuni.exam.service.AstronomerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class AstronomerServiceImpl implements AstronomerService {

    public static final String FILE_PATH_ASTRONOMER = "src/main/resources/files/xml/astronomers.xml";

    private final StarRepository starRepository;
    private final AstronomerRepository astronomerRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;

    public AstronomerServiceImpl(StarRepository starRepository, AstronomerRepository astronomerRepository, ModelMapper modelMapper, XmlParser xmlParser, ValidationUtil validationUtil) {
        this.starRepository = starRepository;
        this.astronomerRepository = astronomerRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;

        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return astronomerRepository.count()>0;
    }

    @Override
    public String readAstronomersFromFile() throws IOException {
        return Files.readString(Path.of(FILE_PATH_ASTRONOMER));
    }

    @Override
    public String importAstronomers() throws IOException, JAXBException {

        StringBuilder sb = new StringBuilder();

//        AstronomersImportRootDto astronomersImportRootDto = xmlParser
//                .fromFile(FILE_PATH_ASTRONOMER,AstronomersImportRootDto.class);

        JAXBContext jaxbContext = JAXBContext.newInstance(AstronomersImportRootDto.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        AstronomersImportRootDto astronomersImportRootDto = (AstronomersImportRootDto)unmarshaller.unmarshal(new File(FILE_PATH_ASTRONOMER)) ;

        System.out.println("2");
        astronomersImportRootDto.getAstronomers().stream()
                .filter(astronomer -> {
                    Optional<Star> starOptional = starRepository.findById(astronomer.getStar());
                    Optional<Astronomer> astronomerOptional =
                            astronomerRepository.findAllByFirstNameAndLastName
                                    (astronomer.getFirstName(),astronomer.getLastName());
                    boolean isValid = validationUtil.isValid(astronomer)
                            && astronomerOptional.isEmpty() && !starOptional.isEmpty();

                    if (isValid ){
                        sb.append(String.format(
                                "Successfully imported astronomer %s %s - %.2f\n",
                                astronomer.getFirstName(),astronomer.getLastName(),astronomer.getAverageObservationHours()
                        ));
                    }else {
                        sb.append("Invalid astronomer\n");
                    }
                    return isValid;
                })
                .map(astronomerImportDto -> {
                    Astronomer astronomer =
                            modelMapper.map(astronomerImportDto,Astronomer.class);
                    astronomer.setObservingStar(starRepository.getById(astronomerImportDto.getStar()));
                    return astronomer;
                }).forEach(astronomerRepository::save);

        return sb.toString();
    }
}
