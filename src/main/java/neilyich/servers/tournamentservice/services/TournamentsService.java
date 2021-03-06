package neilyich.servers.tournamentservice.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.controllers.dto.FilterParameters;
import neilyich.servers.tournamentservice.model.EventType;
import neilyich.servers.tournamentservice.model.PrizeFund;
import neilyich.servers.tournamentservice.model.Tournament;
import neilyich.servers.tournamentservice.model.TournamentType;
import neilyich.servers.tournamentservice.model.projections.TournamentId;
import neilyich.servers.tournamentservice.repositories.EventsRepository;
import neilyich.servers.tournamentservice.repositories.TournamentsRepository;
import neilyich.servers.tournamentservice.services.filters.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TournamentsService {
    private final TournamentsRepository tournamentsRepository;
    private final EventsRepository eventsRepository;
    public List<Tournament> findTournaments(FilterParameters filterParameters) {
        LocalDateTime start = filterParameters.getStartPeriod();
        LocalDateTime end = filterParameters.getEndPeriod();
        if(start == null) {
            start = LocalDateTime.now().minusYears(1000);
        }
        if(end == null) {
            end = LocalDateTime.now().plusYears(1000);
        }
        log.info("start period: {}", start);
        log.info("end period: {}", end);

        List<TournamentId> ids = eventsRepository
                .findAllByDateBetweenAndEventTypeEqualsOrderByDateAsc(start, end, EventType.START_TOURNAMENT);
        log.info("found {} tournaments between dates", ids.size());

        List<Integer> intIds = new ArrayList<>(ids.size());
        ids.forEach(id -> intIds.add(id.getTournamentId()));

        List<Tournament> tournaments = tournamentsRepository.findAllByIdIn(intIds);
        log.info("found {} tournaments by id", tournaments.size());

        List<Tournament> filtered = new LinkedList<>();

        var filters = initFilters(filterParameters);
        for(var t: tournaments) {
            if(t.getPrizeFund() == null) {
                t.setPrizeFund(new PrizeFund());
            }
            if(t.getType() == null) {
                t.setType(new TournamentType());
            }
            boolean ok = true;
            for(var f: filters) {
                if(!f.isSuitable(t)) {
                    ok = false;
                    log.info("tournament is not suitable for filter: {}", t);
                    break;
                }
            }
            if(ok) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    private List<TournamentFilter> initFilters(FilterParameters filterParameters) {
        List<TournamentFilter> filters = new LinkedList<>();
        if(filterParameters.getBilliardTypes() != null && !filterParameters.getBilliardTypes().isEmpty()) {
            filters.add(new BilliardTypeFilter(new HashSet<>(filterParameters.getBilliardTypes())));
        }
        if(filterParameters.getCategories() != null) {
            filters.add(new CategoryFilter(new HashSet<>(filterParameters.getCategories())));
        }
        if(filterParameters.getFree() != null) {
            filters.add(new FreeTournamentFilter(filterParameters.getFree()));
        }
        if(filterParameters.getWithHandicap() != null) {
            filters.add(new WithHandicapFilter(filterParameters.getWithHandicap()));
        }
        return filters;
    }
}
