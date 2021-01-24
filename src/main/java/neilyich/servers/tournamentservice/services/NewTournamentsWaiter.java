package neilyich.servers.tournamentservice.services;

import neilyich.servers.tournamentservice.model.Tournament;

import java.util.List;

public interface NewTournamentsWaiter {
    List<Tournament> waitNewTournaments(long timeout) throws InterruptedException;
    void setTournaments(List<Tournament> tournaments);
}
