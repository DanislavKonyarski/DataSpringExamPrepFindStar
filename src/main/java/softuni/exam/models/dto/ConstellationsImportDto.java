package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class ConstellationsImportDto {
    @Expose
    private String name;
    @Expose
    private String description;

    public ConstellationsImportDto() {
    }
    @Size(min = 3,max = 20)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Size(min = 5)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
