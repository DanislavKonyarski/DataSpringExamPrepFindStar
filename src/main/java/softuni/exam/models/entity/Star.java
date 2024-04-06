package softuni.exam.models.entity;

import softuni.exam.models.enums.StarType;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stars")
public class Star extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    @Column(name = "light_years",nullable = false)
    private Double lightYear;
    @Column(nullable = false, unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "star_type",nullable = false)
    private StarType starType;

    @ManyToOne
    @JoinColumn(name = "Constellation_id",referencedColumnName = "id")
    private Constellation constellation;
    @OneToMany(mappedBy = "observingStar")
    private Set<Astronomer> astronomers;

    public Constellation getConstellation() {
        return constellation;
    }

    public void setConstellation(Constellation constellation) {
        this.constellation = constellation;
    }

    public Star() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLightYear() {
        return lightYear;
    }

    public void setLightYear(Double lightYear) {
        this.lightYear = lightYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StarType getStarType() {
        return starType;
    }

    public void setStarType(StarType starType) {
        this.starType = starType;
    }

    public Set<Astronomer> getAstronomers() {
        return astronomers;
    }

    public void setAstronomers(Set<Astronomer> astronomers) {
        this.astronomers = astronomers;
    }
}
