package neilyich.servers.tournamentservice.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.controllers.dto.FilterParameters;
import neilyich.servers.tournamentservice.model.Tournament;
import neilyich.servers.tournamentservice.services.TournamentsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class TournamentServiceController {

    private final TournamentsService tournamentsService;

    @GetMapping(value = "/tournaments",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<Tournament> tournaments(@RequestBody FilterParameters filterParameters) {
        var result = tournamentsService.findTournaments(filterParameters);
        log.info("found {} tournaments with filterParameters: {}", result.size(), filterParameters);
        return result;
    }

    @GetMapping("/debug")
    public void ox() {
    }
}
