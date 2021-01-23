package neilyich.servers.tournamentservice.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.controllers.dto.FilterParameters;
import neilyich.servers.tournamentservice.model.Tournament;
import neilyich.servers.tournamentservice.services.TournamentsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    public List<Tournament> tournaments(@RequestBody FilterParameters filterParameters) throws IOException {
        var result = tournamentsService.findTournaments(filterParameters);
//        var result = tournamentsRepository.findByStartDateInPeriod(filterParameters.getStartPeriod(),
//                filterParameters.getEndPeriod(), filterParameters.getBilliardType(), filterParameters.getFree(), filterParameters.getCategory(),
//                filterParameters.getWithHandicap(), filterParameters.getTournamentIdFrom());
        log.info("found {} tournaments with filterParameters: {}", result.size(), filterParameters);
        return result;
    }

    @GetMapping("/debug")
    public void ox() throws IOException {
        Element el = Jsoup.parse("<div>\"first text\"<br>\"second text\"</div>");
        log.info(el.selectFirst("div").ownText().replace("\"", ""));
    }
}
