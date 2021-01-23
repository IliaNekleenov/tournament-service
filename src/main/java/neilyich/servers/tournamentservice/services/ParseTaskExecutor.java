package neilyich.servers.tournamentservice.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.model.ParseTournamentsResult;
import neilyich.servers.tournamentservice.parsers.TournamentParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ParseTaskExecutor {
    private final List<TournamentParser> parsers;
    private static final String separator = "------------------------------------------------";

    //@Scheduled(fixedRate = 1000 * 60 * 60)
    public void parseAndSaveTournaments() throws IOException {
        log.info("parsing tournaments...");
        List<ParseTournamentsResult> parseTournamentsResults = new ArrayList<>(parsers.size());
        for(var parser: parsers) {
            parseTournamentsResults.add(parser.parseNewTournaments());

        }
        log.info("finished parsing tournaments");
        for(var result: parseTournamentsResults) {
            log.info(separator);
            log.info(separator);
            log.info("web site: {}", result.getWebSite().name());
            log.info("total parse attempts: {}", result.getTotalParsingAttempts());
            log.info("new parsed tournaments: {}", result.getNewParsedTournaments().size());
            log.info("tournaments already parsed: {}", result.getTotalAlreadyParsed());
            var exceptions = result.getUnexpectedExceptions();
            log.info("unexpected exceptions encountered: {}", exceptions.size());
            for(var thr: exceptions) {
                log.error("", thr);
            }
        }
        log.info(separator);
        log.info(separator);
    }
}
