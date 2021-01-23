package neilyich.servers.tournamentservice.services.filters;

import lombok.AllArgsConstructor;
import neilyich.servers.tournamentservice.model.Tournament;

@AllArgsConstructor
public class FreeTournamentFilter implements TournamentFilter {
    private final boolean isFree;
    @Override
    public boolean isSuitable(Tournament tournament) {
        return tournament.getFree() != null && isFree == tournament.getFree();
    }
}
