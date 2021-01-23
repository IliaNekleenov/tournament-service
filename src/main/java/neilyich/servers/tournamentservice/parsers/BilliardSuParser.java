package neilyich.servers.tournamentservice.parsers;

import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.model.ParseTournamentsResult;
import neilyich.servers.tournamentservice.model.WebSite;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class BilliardSuParser implements TournamentParser {
    private static final String mainUrl = "http://www.billiard.su";
    private static final String target = "/compet.asp";
    @Override
    public ParseTournamentsResult parseNewTournaments() throws IOException {
        return new ParseTournamentsResult(WebSite.BILLIARD_SU);
    }

}
