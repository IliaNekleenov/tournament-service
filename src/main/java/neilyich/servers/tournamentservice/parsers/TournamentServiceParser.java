package neilyich.servers.tournamentservice.parsers;

import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.exceptions.TournamentAlreadyParsedException;
import neilyich.servers.tournamentservice.model.*;
import neilyich.servers.tournamentservice.repositories.TournamentsRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TournamentServiceParser implements TournamentParser {
    private static final String mainUrl = "https://tournamentservice.net";

    private final TournamentsRepository tournamentsRepository;

    @Autowired
    public TournamentServiceParser(TournamentsRepository tournamentsRepository) {
        this.tournamentsRepository = tournamentsRepository;
    }

    private ParseTournamentsResult parseTournamentsResult;

    @Override
    public ParseTournamentsResult parseNewTournaments() throws IOException {
        parseTournamentsResult = new ParseTournamentsResult(WebSite.TOURNAMENT_SERVICE);

        Document mainPage = Jsoup.connect(mainUrl).get();
        Element regEvents = mainPage.selectFirst("section#reg-events.eventgroup.cf");
        Element addEvents = mainPage.selectFirst("section#add-events.eventgroup.cf");

        parseEvents(parseTournamentsResult, addEvents);

        parseEvents(parseTournamentsResult, regEvents);

        return parseTournamentsResult;
    }

    private void parseEvents(ParseTournamentsResult parseTournamentsResult, Element events) {
        if(events != null) {
            Element div = events.selectFirst("div");
            Elements divs = div.children();
            Elements tournamentDivs = divs.select("div");
            for(Element el: tournamentDivs) {
                parseTournamentsResult.incTotalParsingAttempts();
                try {
                    Tournament tournament = parseTournament(el);

                    tournament.setWebSite(WebSite.TOURNAMENT_SERVICE);
                    tournament.setParsedDate(LocalDateTime.now());
                    //tournament.setRegistrationStarted(false);
                    tournamentsRepository.save(tournament);
                    parseTournamentsResult.getNewParsedTournaments().add(tournament);
                }
                catch (TournamentAlreadyParsedException e) {
                    log.info(e.getMessage());
                    parseTournamentsResult.incTotalAlreadyParsed();
                }
                catch (Throwable e) {
                    log.error("Unexpected exception while parsing tournament: {}", el.outerHtml(), e);
                    parseTournamentsResult.getUnexpectedExceptions().add(e);
                }
            }
        }
    }

    private Tournament parseTournament(Element tournamentInfo) throws IOException, TournamentAlreadyParsedException {
        Tournament tournament = new Tournament();
        Element img = tournamentInfo.selectFirst("img");
        String address = img.attributes().get("alt");
        log.info("parsed country: {}", address);
        tournament.setAddress(address);

        LocalDate startDate = LocalDate.parse(tournamentInfo.selectFirst("i").ownText(), DateTimeFormatter.ofPattern("dd.MM.yy"));
        EventDate startEvent = EventDate.builder()
                .date(localDateToLocalDateTime(startDate))
                .eventType(EventType.START_TOURNAMENT).build();
        log.info("parsed startEvent: {}", startEvent);
        List<EventDate> dts = new LinkedList<>();
        dts.add(startEvent);
        tournament.setDates(dts);

        Element link = tournamentInfo.selectFirst("a[href]");
        String name = link.ownText();
        log.info("parsed name: {}", name);
        tournament.setName(name);

        Category category = Category.of(name);
        log.info("parsed category: {}", category);
        tournament.setCategory(category);

        String join = "/";
        String target = link.attributes().get("href");
        String href;
        if(target.startsWith("//")) {
            target = "http:" + target;
        }
        if(target.startsWith("http")) {
            href = target;
            tournament.setHref(href);
            Tournament found = tournamentsRepository.findByNameAndHrefAndWebSite(tournament.getName(), href, WebSite.TOURNAMENT_SERVICE);
            if(found != null) {
                throw new TournamentAlreadyParsedException("already parsed tournament: " + href);
            }
            log.info("parsed absolute href: {}", href);
            tournament.setType(TournamentType.builder()
                    .type(BilliardType.of(tournament.getName())).build());
            return tournament;
        }
        else {
            if(target.startsWith(join)) {
                join = "";
            }
            href = mainUrl + join + target;
            tournament.setHref(href);
        }

        log.info("parsed href: {}", href);
        Tournament found = tournamentsRepository.findByNameAndHrefAndWebSite(tournament.getName(), href, WebSite.TOURNAMENT_SERVICE);
        if(found != null) {
            throw new TournamentAlreadyParsedException("already parsed tournament: " + href);
        }

        Document tournamentInitialPage = Jsoup.connect(href).get();
        Element loadInfo = tournamentInitialPage.selectFirst("iframe#datacell");
        String src = loadInfo.attributes().get("src");
        join = "/";
        if(src.startsWith(join)) {
            join = "";
        }
        log.info("parsed content src: {}", src);
        Document tournamentPage = Jsoup.connect(mainUrl + join + src).get();


        Element tabParticipants = tournamentPage.selectFirst("section#tab-participants");
        EventDate endRegEvent = null;
        if(tabParticipants != null) {
            Element btn = tabParticipants.selectFirst("input");
            if(btn != null) {
                String endRegInfo = btn.attributes().get("value");
                endRegInfo = endRegInfo.toLowerCase().replace("registration till", "").replace("онлайн регистрация до", "").trim();
                try {
                    LocalDateTime endRegDate = LocalDateTime.parse(endRegInfo, DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
                    endRegEvent = EventDate.builder()
                            .date(endRegDate)
                            .eventType(EventType.END_REG).build();
                }
                catch (Exception e) {
                    log.warn("could not parse date: {}", endRegInfo, e);
                    parseTournamentsResult.getUnexpectedExceptions().add(e);
                }
            }
        }
        else {
            log.info("no participants tab found");
        }
        log.info("parsed endRegEvent: {}", endRegEvent);
        if(endRegEvent != null) {
            tournament.getDates().add(endRegEvent);
        }

        Element tabRegulations = tournamentPage.selectFirst("section#tab-regulations");

        Map<String, String> infoMap = new HashMap<>();
        if(tabRegulations != null) {
            infoMap.putAll(parseRegulations(tabRegulations));
        }


        Element tabInfo = tournamentPage.selectFirst("section#tab-info");
        if(tabInfo != null) {
            infoMap.putAll(parseTabInfo(tabInfo));
        }
        fillTournamentWithInfoMap(infoMap, tournament);

        return tournament;
    }

    private void fillTournamentWithInfoMap(Map<String, String> infoMap, Tournament tournament) {
        log.info("infoMap final: {}", infoMap.toString());
        if(infoMap.containsKey("место проведения")) {
            tournament.setAddress(tournament.getAddress() + ", " + infoMap.get("место проведения"));
        }

        BilliardType type = null;
        String discipline = null;
        if(infoMap.containsKey("дисциплина")) {
            discipline = infoMap.get("дисциплина");
            type = BilliardType.of(discipline);
        }
        else if(infoMap.containsKey("тип игры")) {
            discipline = infoMap.get("тип игры");
            type = BilliardType.of(discipline);
        }
        if(type == null) {
            type = BilliardType.of(tournament.getName());
        }
        tournament.setType(TournamentType.builder()
                .type(type)
                .discipline(discipline).build());

        if(infoMap.containsKey("начало турнира")) {
            String startStr = infoMap.get("начало турнира");
            try {
                LocalDateTime date = LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("EEEE dd.MM.yy HH:mm"));
                tournament.getDates().get(0).setDate(date);
            }
            catch (Exception e) {
                log.warn("could not parse date: {}", startStr, e);
                parseTournamentsResult.getUnexpectedExceptions().add(e);
            }
        }
        else if(infoMap.containsKey("дата начала")) {
            String startStr = infoMap.get("дата начала");
            try {
                LocalDate date = LocalDate.parse(startStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                tournament.getDates().get(0).setDate(localDateToLocalDateTime(date));
            }
            catch (Exception e) {
                log.warn("could not parse date: {}", startStr, e);
                parseTournamentsResult.getUnexpectedExceptions().add(e);
            }
        }

        if(infoMap.containsKey("окончание турнира")) {
            String endStr = infoMap.get("окончание турнира");
            try {
                LocalDate date = LocalDate.parse(endStr, DateTimeFormatter.ofPattern("dd.MM.yy"));
                tournament.getDates().add(EventDate.builder()
                        .date(localDateToLocalDateTime(date))
                        .eventType(EventType.END_TOURNAMENT).build());
            }
            catch (Exception e) {
                log.warn("could not parse date: {}", endStr, e);
                parseTournamentsResult.getUnexpectedExceptions().add(e);
            }
        }
        else if(infoMap.containsKey("дата окончания")) {
            String endStr = infoMap.get("дата окончания");
            try {
                LocalDate date = LocalDate.parse(endStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                tournament.getDates().add(EventDate.builder()
                        .date(localDateToLocalDateTime(date))
                        .eventType(EventType.END_TOURNAMENT).build());
            }
            catch (Exception e) {
                log.warn("could not parse date: {}", endStr, e);
                parseTournamentsResult.getUnexpectedExceptions().add(e);
            }
        }

        if(infoMap.containsKey("призовой фонд")) {
            tournament.setPrizeFund(PrizeFund.builder()
                    .info(infoMap.get("призовой фонд")).build());
        }

        if(tournament.getCategory() == Category.ALL && infoMap.containsKey("участники")) {
            String val = infoMap.get("участники");
            tournament.setCategory(Category.of(val));
        }

        if(infoMap.containsKey("условия проведения")) {
            tournament.setDescription(infoMap.get("условия проведения"));
        }

    }


    private Map<String, String> parseRegulations(Element tabRegulations) {
        Element p = tabRegulations.selectFirst("p");
        String[] lines = p.html()
                .replace("<br>", "\n")
                .replace("<strong>", "")
                .replace("</strong>", "").split("\n");
        Map<String, String> infoMap = new HashMap<>();
        for(String line: lines) {
            String[] pair = line.split(":");
            if(pair.length != 2) {
                continue;
            }
            String key = pair[0].trim().toLowerCase();
            String val = pair[1].trim();
            if(val.endsWith(".")) {
                val = val.substring(0, val.length() - 1);
            }
            infoMap.put(key, val);
        }
        return infoMap;
    }

    private Map<String,String> parseTabInfo(Element tabInfo) {
        Element list = tabInfo.selectFirst("dl");
        Elements keys = list.select("dt");
        Elements values = list.select("dd");

        Map<String, String> infoMap = new HashMap<>(keys.size());
        if(keys.size() + 1 == values.size() || keys.size() == values.size()) {
            int dif = values.size() - keys.size(); // 0 or 1
            for(int i = 0; i < keys.size(); i++) {
                infoMap.put(keys.get(i).ownText().toLowerCase(), values.get(i + dif).ownText());
            }
        }
        else {
            log.warn("different sizes: {}, {}", keys.size(), values.size());
        }

        log.info("infoMap: {}", infoMap.toString());

        return infoMap;
    }

    private LocalDateTime localDateToLocalDateTime(LocalDate date) {
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = dateTime.minusHours(dateTime.getHour())
                .minusMinutes(dateTime.getMinute())
                .minusSeconds(dateTime.getSecond())
                .minusNanos(dateTime.getNano());
        return LocalDateTime.of(date, dateTime.toLocalTime());

    }
}
