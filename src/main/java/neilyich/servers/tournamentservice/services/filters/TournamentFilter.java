package neilyich.servers.tournamentservice.services.filters;

import neilyich.servers.tournamentservice.model.Tournament;

public interface TournamentFilter {
    boolean isSuitable(Tournament tournament);
}
