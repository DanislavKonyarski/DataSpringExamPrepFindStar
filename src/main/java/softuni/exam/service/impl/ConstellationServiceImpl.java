package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ConstellationsImportDto;
import softuni.exam.models.entity.Constellation;
import softuni.exam.repository.ConstellationRepository;
import softuni.exam.service.ConstellationService;
import softuni.exam.util.ValidationUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConstellationServiceImpl implements ConstellationService {

    public static final String PATH_FILE_CONSTELLATIONS = "src/main/resources/files/json/constellations.json";

    private ConstellationRepository constellationRepository;
    private ModelMapper modelMapper;
    private ValidationUtil validationUtil;

    private Gson gson;

    public ConstellationServiceImpl(ConstellationRepository constellationRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson) {
        this.constellationRepository = constellationRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return constellationRepository.count()>0;
    }

    @Override
    public String readConstellationsFromFile() throws IOException {
        String readConstellationsJson = Files.readString(Path.of(PATH_FILE_CONSTELLATIONS));


        return readConstellationsJson;
    }

    @Override
    public String importConstellations() throws IOException {

        StringBuilder sb = new StringBuilder();

        String jsonString =  Files.readString(Path.of(PATH_FILE_CONSTELLATIONS));

        ConstellationsImportDto[] constellationsImportDtos =
                gson.fromJson(jsonString, ConstellationsImportDto[].class);


        Arrays.stream(constellationsImportDtos)
                .filter(constellationsImportDto -> {
                    Optional<Constellation> constellation = constellationRepository.findByName(constellationsImportDto.getName());
                    boolean isValid = validationUtil.isValid(constellationsImportDto) && constellation.isEmpty();

                    if (isValid){
                        sb.append(String.format("Successfully imported constellation %s - %s\n"
                                ,constellationsImportDto.getName(),constellationsImportDto.getDescription()));
                    }else {
                        sb.append("Invalid constellation\n");
                    }

                        return isValid;
                })
                .map(constellationsImportDto ->
                        modelMapper.map(constellationsImportDto, Constellation.class))
                .forEach(constellationRepository::save);


        return sb.toString();
    }
}
