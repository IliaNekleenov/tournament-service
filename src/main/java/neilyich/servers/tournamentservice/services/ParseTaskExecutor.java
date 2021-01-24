package neilyich.servers.tournamentservice.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.model.ParseTournamentsResult;
import neilyich.servers.tournamentservice.model.Tournament;
import neilyich.servers.tournamentservice.parsers.TournamentParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ParseTaskExecutor {
    private final List<TournamentParser> parsers;
    private final TournamentWaitersManager manager;
    private static final String separator = "------------------------------------------------";

    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void parseAndSaveTournaments() throws IOException {
        log.info("parsing tournaments...");
        List<ParseTournamentsResult> parseTournamentsResults = new ArrayList<>(parsers.size());
        for(var parser: parsers) {
            parseTournamentsResults.add(parser.parseNewTournaments());

        }
        List<Tournament> tournaments = new LinkedList<>();
        log.info("finished parsing tournaments");
        for(var result: parseTournamentsResults) {
            log.info(separator);
            log.info(separator);
            log.info("web site: {}", result.getWebSite().name());
            var exceptions = result.getUnexpectedExceptions();
            log.info("unexpected exceptions encountered: {}", exceptions.size());
            for(var thr: exceptions) {
                log.error("", thr);
            }
        }
        log.info("\n\n\n\n\n\n\n\n");
        for(var result: parseTournamentsResults) {
            log.info(separator);
            log.info(separator);
            log.info("web site: {}", result.getWebSite().name());
            log.info("total parse attempts: {}", result.getTotalParsingAttempts());
            log.info("new parsed tournaments: {}", result.getNewParsedTournaments().size());
            log.info("tournaments already parsed: {}", result.getTotalAlreadyParsed());
            var exceptions = result.getUnexpectedExceptions();
            log.info("unexpected exceptions encountered: {}", exceptions.size());
            tournaments.addAll(result.getNewParsedTournaments());
        }
        log.info(separator);
        log.info(separator);
        if(tournaments.isEmpty()) {
            return;
        }

        manager.notifyWaiters(tournaments);
    }
}
