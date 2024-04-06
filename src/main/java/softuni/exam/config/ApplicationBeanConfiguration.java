package softuni.exam.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import softuni.exam.models.dto.StarImportDto;
import softuni.exam.models.entity.Star;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ApplicationBeanConfiguration {

    @Bean
    public Gson gson(){
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    }

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();


        Converter<String, LocalDate> localDateConverter = new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(MappingContext<String, LocalDate> mappingContext) {
                return mappingContext.getSource()==null
                        ? LocalDate.now()
                        : LocalDate.parse(mappingContext.getSource(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            }
        };

        modelMapper.addConverter(localDateConverter);

        modelMapper.createTypeMap(StarImportDto.class, Star.class)
                .addMappings(mapper ->
                        mapper.map(StarImportDto::getLightYears,
                                Star::setLightYear));


        return modelMapper;
    }



}
