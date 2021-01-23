package neilyich.servers.tournamentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TournamentType implements Serializable {
    @Column(name = "billiard_type")
    @Enumerated(EnumType.STRING)
    private BilliardType type;
    @Column(name = "discipline")
    private String discipline;
}
