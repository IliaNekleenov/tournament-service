package neilyich.servers.tournamentservice.services.filters;

import lombok.AllArgsConstructor;
import neilyich.servers.tournamentservice.model.EventType;
import neilyich.servers.tournamentservice.model.Tournament;

import java.time.LocalDateTime;

@AllArgsConstructor
public class StartDateFilter implements TournamentFilter {
    private final LocalDateTime startPeriod;
    private final LocalDateTime endPeriod;
    @Override
    public boolean isSuitable(Tournament tournament) {
        var dates = tournament.getDates();
        for(var date: dates) {
            if(date.getEventType().equals(EventType.START_TOURNAMENT)) {
                return date.getDate().isAfter(startPeriod) && date.getDate().isBefore(endPeriod);
            }
        }
        return false;
    }
}
