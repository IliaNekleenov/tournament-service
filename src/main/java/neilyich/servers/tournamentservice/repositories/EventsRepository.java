package neilyich.servers.tournamentservice.repositories;

import neilyich.servers.tournamentservice.model.EventDate;
import neilyich.servers.tournamentservice.model.EventType;
import neilyich.servers.tournamentservice.model.projections.TournamentId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventsRepository extends PagingAndSortingRepository<EventDate, Integer> {
    List<TournamentId> findAllByDateBetweenAndEventTypeEqualsOrderByDateAsc(LocalDateTime startPeriod, LocalDateTime endPeriod, EventType type);
}
