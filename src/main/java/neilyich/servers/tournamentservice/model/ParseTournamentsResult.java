package neilyich.servers.tournamentservice.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Getter
public class ParseTournamentsResult implements Serializable {
    private final List<Throwable> unexpectedExceptions;
    private final List<Tournament> newParsedTournaments;
    private int totalParsingAttempts;
    private int totalAlreadyParsed;
    private final WebSite webSite;

    public ParseTournamentsResult(WebSite webSite) {
        newParsedTournaments = new LinkedList<>();
        unexpectedExceptions = new LinkedList<>();
        totalParsingAttempts = 0;
        totalAlreadyParsed = 0;
        this.webSite = webSite;
    }

    public void incTotalParsingAttempts() {
        totalParsingAttempts++;
    }

    public void incTotalAlreadyParsed() {
        totalAlreadyParsed++;
    }
}
