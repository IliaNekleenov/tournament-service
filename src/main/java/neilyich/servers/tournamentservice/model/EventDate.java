package neilyich.servers.tournamentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class EventDate implements Serializable {
    @Id
    @Column(name = "id")
    @JsonIgnore
    private UUID id;

    @JsonIgnore
    @Column(name = "tournament_id")
    private UUID tournamentId;

    @Column(name = "event_name")
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    @Column(name = "date")
    private LocalDateTime date;
}
