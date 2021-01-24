package neilyich.servers.tournamentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tournaments")
public class Tournament implements Serializable {
    @Id
    private UUID id;

    @Embedded
    private TournamentType type;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "club_id", referencedColumnName = "id")
    private Club club;

    @Column(name = "url")
    private String href;
    @Column(name = "description")
    private String description;
    @Column(name = "fee_info")
    private String feeInfo;

    @Column(name = "free_form")
    private Boolean freeForm;
    @Column(name = "with_handicap")
    private Boolean withHandicap;
    @Column(name = "is_free")
    private Boolean free;
//    private Boolean registrationStarted;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "max_participants")
    private Integer maxParticipants;
    @Column(name = "max_age")
    private Integer maxAge;
    @Column(name = "min_age")
    private Integer minAge;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "tournament_id", referencedColumnName = "id")
    private List<EventDate> dates;

    @Embedded
    private PrizeFund prizeFund;

    @JsonIgnore
    @Column(name = "web_site")
    @Enumerated(EnumType.STRING)
    private WebSite webSite;

    @JsonIgnore
    @Column(name = "parsed_date")
    private LocalDateTime parsedDate;


}
