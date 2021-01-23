package neilyich.servers.tournamentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PrizeFund implements Serializable {
    @Column(name = "prize_fund_info")
    private String info;
    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
