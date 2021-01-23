package neilyich.servers.tournamentservice.services.filters;

import lombok.AllArgsConstructor;
import neilyich.servers.tournamentservice.model.Tournament;

@AllArgsConstructor
public class WithHandicapFilter implements TournamentFilter {
    private final boolean withHandicap;
    @Override
    public boolean isSuitable(Tournament tournament) {
        return tournament.getWithHandicap() != null && withHandicap == tournament.getWithHandicap();
    }
}
