package neilyich.servers.tournamentservice.services.filters;

import lombok.AllArgsConstructor;
import neilyich.servers.tournamentservice.model.BilliardType;
import neilyich.servers.tournamentservice.model.Tournament;

import java.util.Set;

@AllArgsConstructor
public class BilliardTypeFilter implements TournamentFilter {
    private final Set<BilliardType> types;
    @Override
    public boolean isSuitable(Tournament tournament) {
        if(tournament.getType() == null) {
            return false;
        }
        return types.contains(tournament.getType().getType());
    }
}
