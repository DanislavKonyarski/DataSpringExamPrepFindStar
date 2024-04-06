package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.StarImportDto;
import softuni.exam.models.entity.Star;
import softuni.exam.repository.ConstellationRepository;
import softuni.exam.repository.StarRepository;
import softuni.exam.service.StarService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import static softuni.exam.models.enums.StarType.RED_GIANT;

@Service
public class StarServiceImpl implements StarService {

    public static final String PATH_FILE_STAR = "src/main/resources/files/json/stars.json";


    private StarRepository starRepository;
    private ModelMapper modelMapper;
    private ValidationUtil validationUtil;
    private Gson gson;
    private ConstellationRepository constellationRepository;

    public StarServiceImpl(StarRepository starRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, ConstellationRepository constellationRepository) {
        this.starRepository = starRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.constellationRepository = constellationRepository;
    }

    @Override
    public boolean areImported() {
        return starRepository.count()>0;
    }

    @Override
    public String readStarsFileContent() throws IOException {
        return Files.readString(Path.of(PATH_FILE_STAR));
    }

    @Override
    public String importStars() throws IOException {

        StringBuilder sb = new StringBuilder();

        StarImportDto[] starImportDtos =
                gson.fromJson(Files.readString
                        (Path.of(PATH_FILE_STAR)), StarImportDto[].class);

        Arrays.stream(starImportDtos).filter(starImportDto -> {
                    Optional<Star> optional = starRepository.findByName(starImportDto.getName());
            boolean isValid = validationUtil.isValid(starImportDto) && optional.isEmpty();
            if(isValid){
                sb.append(String.format("Successfully imported star %s - %.2f light years\n"
                        ,starImportDto.getName(),starImportDto.getLightYears()));
            }else {
                sb.append("Invalid star\n");
            }
            return isValid;
        }).map(starImportDto -> {
                    Star star = modelMapper.map(starImportDto, Star.class);
                    star.setConstellation(constellationRepository
                                    .getById(starImportDto.getConstellation()));
                    return star;
                })
                .forEach(starRepository::save);

        return sb.toString();
    }

    @Override
    public String exportStars() {
        StringBuilder stringBuilder = new StringBuilder();

        starRepository.findAllByStarTypeAndAstronomersNullOrderByLightYear(RED_GIANT)
                .forEach(star -> {
                    stringBuilder.append(String.format(
                            "Star: %s\n" +
                                    "   *Distance: %.2f light years\n" +
                                    "   **Description: %s\n" +
                                    "   ***Constellation: %s\n"
                            ,star.getName(),star.getLightYear(),star.getDescription()
                            ,star.getConstellation().getName()
                    ));
                });

        return stringBuilder.toString();
    }
}
