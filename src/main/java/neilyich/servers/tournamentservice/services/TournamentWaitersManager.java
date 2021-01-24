package neilyich.servers.tournamentservice.services;

import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.model.Tournament;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class TournamentWaitersManager {
    private final List<NewTournamentsWaiter> waiters = new LinkedList<>();
    public synchronized void addWaiter(NewTournamentsWaiter waiter) {
        waiters.add(waiter);
    }

    public synchronized void notifyWaiters(List<Tournament> tournaments) {
        var copy = new ArrayList<>(tournaments);
        for(var w: waiters) {
            w.setTournaments(copy);
        }
        waiters.clear();
    }
}
