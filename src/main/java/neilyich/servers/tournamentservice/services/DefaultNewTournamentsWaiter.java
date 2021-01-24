package neilyich.servers.tournamentservice.services;

import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.model.Tournament;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DefaultNewTournamentsWaiter implements NewTournamentsWaiter {
    private List<Tournament> tournaments = new ArrayList<>();

    @Override
    public synchronized List<Tournament> waitNewTournaments(long timeout) throws InterruptedException {
        if(tournaments.isEmpty()) {
            wait(timeout);
        }
        return tournaments;
    }

    @Override
    public synchronized void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
        notify();
    }
}
