package neilyich.servers.tournamentservice.parsers;

import neilyich.servers.tournamentservice.model.ParseTournamentsResult;

import java.io.IOException;

public interface TournamentParser {
    ParseTournamentsResult parseNewTournaments() throws IOException;
}
