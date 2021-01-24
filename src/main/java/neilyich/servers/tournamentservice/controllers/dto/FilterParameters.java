package neilyich.servers.tournamentservice.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import neilyich.servers.tournamentservice.model.BilliardType;
import neilyich.servers.tournamentservice.model.Category;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FilterParameters implements Serializable {
    @JsonProperty(value = "startPeriod")
    private LocalDateTime startPeriod;
    @JsonProperty("endPeriod")
    private LocalDateTime endPeriod;
    @JsonProperty("billiardTypes")
    private List<BilliardType> billiardTypes;
    @JsonProperty("categories")
    private List<Category> categories;
    @JsonProperty("withHandicap")
    private Boolean withHandicap;
    @JsonProperty("isFree")
    private Boolean free;
    @JsonProperty("cities")
    private List<String> cities;
}
