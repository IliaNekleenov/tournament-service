package neilyich.servers.tournamentservice.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.controllers.dto.FilterParameters;
import neilyich.servers.tournamentservice.model.Club;
import neilyich.servers.tournamentservice.model.Tournament;
import neilyich.servers.tournamentservice.repositories.ClubsRepository;
import neilyich.servers.tournamentservice.services.DefaultNewTournamentsWaiter;
import neilyich.servers.tournamentservice.services.TournamentWaitersManager;
import neilyich.servers.tournamentservice.services.TournamentsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class TournamentServiceController {

    private final TournamentsService tournamentsService;
    private final ClubsRepository clubsRepository;

    private final TournamentWaitersManager manager;

    @GetMapping(value = "/tournaments",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<Tournament> tournaments(@RequestBody FilterParameters filterParameters) {
        var result = tournamentsService.findTournaments(filterParameters);
        log.info("found {} tournaments with filterParameters: {}", result.size(), filterParameters);
        return result;
    }

    @GetMapping(value = "/tournaments/new",
            produces = "application/json;charset=UTF-8")
    public List<Tournament> newTournaments(@RequestParam(name = "timeout", required = false, defaultValue = "0") Long timeout,
                                           @RequestBody FilterParameters filterParameters)
            throws InterruptedException {
        var waiter = new DefaultNewTournamentsWaiter();
        manager.addWaiter(waiter);
        log.info("waiting for new parsed tournaments...");
        var newTournaments = waiter.waitNewTournaments(timeout);
        log.info("got new tournaments");
        return tournamentsService.filter(newTournaments, filterParameters);
    }

    @GetMapping(value = "/clubs/all",
            produces = "application/json;charset=UTF-8")
    public Iterable<Club> clubs() {
        return clubsRepository.findAll();
    }


    @GetMapping("/debug")
    public void ox() {
    }
}
