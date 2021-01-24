package neilyich.servers.tournamentservice.repositories;

import neilyich.servers.tournamentservice.model.EventDate;
import neilyich.servers.tournamentservice.model.EventType;
import neilyich.servers.tournamentservice.model.projections.TournamentId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventsRepository extends PagingAndSortingRepository<EventDate, UUID> {
    List<TournamentId> findAllByDateBetweenAndEventTypeEqualsOrderByDateAsc(LocalDateTime startPeriod, LocalDateTime endPeriod, EventType type);
}
