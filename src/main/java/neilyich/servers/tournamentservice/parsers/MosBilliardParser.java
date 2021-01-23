package neilyich.servers.tournamentservice.parsers;

import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.model.ParseTournamentsResult;
import neilyich.servers.tournamentservice.model.WebSite;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class MosBilliardParser implements TournamentParser {
    @Override
    public ParseTournamentsResult parseNewTournaments() throws IOException {
        return new ParseTournamentsResult(WebSite.MOS_BILLIARD);
    }
}
