package neilyich.servers.tournamentservice.parsers;

import lombok.extern.slf4j.Slf4j;
import neilyich.servers.tournamentservice.exceptions.ParseException;
import neilyich.servers.tournamentservice.exceptions.TournamentAlreadyParsedException;
import neilyich.servers.tournamentservice.model.*;
import neilyich.servers.tournamentservice.repositories.TournamentsRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LlbSuParser implements TournamentParser {
    private static final String mainUrl = "https://llb.su";
    private static final String nextTournamentsTarget = "/tournaments/next";

    private final TournamentsRepository tournamentsRepository;

    private ParseTournamentsResult parseTournamentsResult;

    @Autowired
    public LlbSuParser(TournamentsRepository tournamentsRepository) {
        this.tournamentsRepository = tournamentsRepository;
    }

    @Override
    public ParseTournamentsResult parseNewTournaments() throws IOException {
        parseTournamentsResult = new ParseTournamentsResult(WebSite.LLB_SU);

        Document mainPage = Jsoup.connect(mainUrl + nextTournamentsTarget).get();

        Elements containers = mainPage.select("div.comp-teaser-container");
        for (int i = containers.size() - 1; i >= 0; i--) {
            parseTournamentsResult.incTotalParsingAttempts();
            Element l = containers.get(i).selectFirst("div.comp-teaser.links-new");
            Element link = l.selectFirst("a[href]");
            String href = link.attributes().get("href");
            log.info("href: {}", href);
            String url = mainUrl + href;

            try {
                Tournament tournament = parseTournament(url);
                if(tournament.getType().getType() == null) {
                    var type = BilliardType.of(tournament.getName());
                    if(type == null) {
                        log.warn("could not parse billiard type for tournament '{}': {}", tournament.getName(), url);
                    }
                    else {
                        tournament.getType().setType(type);
                    }
                }
                tournament.setHref(url);
                tournament.setWebSite(WebSite.LLB_SU);
                tournament.setParsedDate(LocalDateTime.now());
                boolean hasStartDate = false;
                for(var ev: tournament.getDates()) {
                    if(ev.getEventType().equals(EventType.START_TOURNAMENT)) {
                        hasStartDate = true;
                        break;
                    }
                }
                if(!hasStartDate) {
                    log.warn("start date not found on tournament page: {}", url);
                    Element dateDiv = containers.get(i).selectFirst("td.date");
                    if(dateDiv == null) {
                        throw new ParseException("could not parse start date of tournament: " + tournament.getName());
                    }
                    else {
                        String str = dateDiv.ownText();
                        LocalDateTime dateTime = LocalDateTime.parse(str, DateTimeFormatter.ofPattern("dd.MM.yy HH.mm"));
                        tournament.getDates().add(0, EventDate.builder()
                                .date(dateTime)
                                .eventType(EventType.START_TOURNAMENT).build());
                    }
                }

                tournamentsRepository.save(tournament);
                parseTournamentsResult.getNewParsedTournaments().add(tournament);
            }
            catch (TournamentAlreadyParsedException e) {
                log.info("already parsed tournament: ", e);
                parseTournamentsResult.incTotalAlreadyParsed();
            }
            catch (Throwable e) {
                log.error("unexpected exception while parsing tournament: {}", url, e);
                parseTournamentsResult.getUnexpectedExceptions().add(e);
            }

        }
        return parseTournamentsResult;
    }

    private Tournament parseTournament(String url) throws IOException, TournamentAlreadyParsedException {
        Document tournamentPage = Jsoup.connect(url).get();

        String name = tournamentPage.selectFirst("h1.title").text();
        log.info("parsed name: {}", name);

        var foundTournament = tournamentsRepository.findByNameAndHrefAndWebSite(name, url, WebSite.LLB_SU);
        if(foundTournament != null) {
            throw new TournamentAlreadyParsedException("already parsed tournament: " + url);
        }

        Element fields = tournamentPage.selectFirst("fieldset.fieldgroup.group-conditions.collapsible");

        Element clubRef = fields.selectFirst("div.field.field-type-nodereference.field-field-clubreference");
        Element clubLink = clubRef.selectFirst("h2.title").selectFirst("a");

        Document clubPage = Jsoup.connect(mainUrl + clubLink.attributes().get("href")).get();
        String address = getAddress(clubPage);
        String clubName = clubLink.text();
        log.info("parsed address: {}", address);
        log.info("parsed clubName: {}", clubName);


        String billiardTypeStr = fields.selectFirst("div.field.field-type-text.field-field-comptype")
                .selectFirst("div.field-item.odd").ownText();
        log.info("parsed billiardTypeStr: {}", billiardTypeStr);
        BilliardType type = BilliardType.of(billiardTypeStr);
        log.info("parsed billiardType: {}", type);

        Boolean freeForm = null;
        Element clothesDiv = fields.selectFirst("div.field.field-type-text.field-field-comp-clothes");
        if(clothesDiv != null) {
            String clothes = clothesDiv.selectFirst("div.field-item.odd").ownText();
            freeForm = "свободная".equals(clothes.toLowerCase());
        }
        log.info("parsed freeForm: {}", freeForm);

        Element maxParts = fields.selectFirst("div.field.field-type-number-integer.field-field-max-parts");
        Element maxPartsDiv = maxParts == null ? null : maxParts.selectFirst("div.field-item.odd");
        String maxParticipantsStr = maxPartsDiv == null ? null : maxPartsDiv.ownText();
        log.info("parsed maxParticipantsStr: {}", maxParticipantsStr);

        Integer maxParticipants = maxParticipantsStr == null ? null : Integer.parseInt(maxParticipantsStr);

        Element feeDiv = fields.selectFirst("div.field.field-type-text.field-field-comp-fee");
        String feeInfo = null;
        Boolean free = null;
        if(feeDiv != null) {
            feeInfo = feeDiv.selectFirst("div.field-item.odd").ownText();
            String str = feeInfo.toLowerCase();
            free = str.contains("бесплатн") || str.equals("нет");
        }
        log.info("parsed feeInfo: {}", feeInfo);

        String descriptionStr = null;
        Element description = fields.selectFirst("div.field.field-type-text.field-field-comp-reglament");
        if(description != null) {
            descriptionStr = description.selectFirst("div.field-item.odd").ownText();
        }
        log.info("parsed descriptionStr: {}", descriptionStr);

        PrizeFund prizeFund = null;
        Element prizeFundDiv = fields.selectFirst("div.field.field-type-text.field-field-comp-prize");
        if(prizeFundDiv != null) {
            String info = prizeFundDiv.selectFirst("div.field-item.odd").ownText();
            prizeFund = PrizeFund.builder().info(info).build();
        }
        log.info("parsed prizeFund: {}", prizeFund);

//        Boolean registrationStarted = null;
//        Element regDiv = tournamentPage.selectFirst("div.field.field-type-text.field-field-register");
//        if(regDiv != null) {
//            String text = regDiv.selectFirst("div.field-item.odd").ownText();
//            registrationStarted = "открыта регистрация".equals(text.toLowerCase());
//        }
//        log.info("parsed registrationStarted: {}", registrationStarted);

        List<EventDate> eventDates = getEventDates(tournamentPage);
        log.info("parsed dates: {}", eventDates);

        Element disciplineDiv = tournamentPage.selectFirst("fieldset.fieldgroup.group-comp-disciplines.collapsible");
        String discipline = null;
        if(disciplineDiv != null) {
            Element div = disciplineDiv.selectFirst("div.field-item.odd");
            if(div != null) {
                discipline = div.text();
            }
        }
        log.info("parsed discipline: {}", discipline);

        Category category = Category.of(name);
        log.info("parsed category: {}", category);

        int containsELO = 0;
        Element addInfo = tournamentPage.selectFirst("fieldset.fieldgroup.group-more.collapsible");
        if(addInfo != null) {
            containsELO = StringUtils.countOccurrencesOf(addInfo.text().toLowerCase(), "эло");
        }
        boolean withHandicap = containsELO > 0;
        log.info("parsed withHandicap: {}", withHandicap);

        return Tournament.builder()
                .name(name)
                .clubName(clubName)
                .address(address)
                .type(TournamentType.builder()
                        .type(type)
                        .discipline(discipline)
                        .build())
                .freeForm(freeForm)
                .maxParticipants(maxParticipants)
                .feeInfo(feeInfo)
                .free(free)
                .dates(eventDates == null ? new ArrayList<>() : eventDates)
                .category(category)
                .withHandicap(withHandicap)
                .description(descriptionStr)
                .prizeFund(prizeFund)
//                .registrationStarted(registrationStarted)
                .build();
    }

    private List<EventDate> getEventDates(Document tournamentPage) {
        Element element = tournamentPage.selectFirst("fieldset.fieldgroup.group-dates.collapsible");
        if(element == null) {
            return null;
        }

        Elements names = element.select("div.field-label-inline-first");
        Elements dates = element.select("span.date-display-single");

        if(names.size() != dates.size()) {
            log.warn("different sizes: {} {}", names.size(), dates.size());
            return null;
        }

        List<EventDate> eventDates = new ArrayList<>(names.size());
        for(int i = 0; i < names.size(); i++) {
            String dateStr = dates.get(i).text();
            LocalDateTime dateTime = parseDate(dateStr);
            String name = names.get(i).text().trim().replace(":", "");
            try {
                eventDates.add(EventDate.builder()
                        .eventType(EventType.of(name))
                        .date(dateTime).build());
            }
            catch (Throwable e) {
                log.error("unexpected exception: ", e);
                parseTournamentsResult.getUnexpectedExceptions().add(e);
            }
        }
        return eventDates;
    }

    private String getAddress(Document clubPage) {
        String address = "";
        Element cityDiv = clubPage.selectFirst("div.field.field-type-text.field-field-club-city");
        if(cityDiv != null) {
            Element div = cityDiv.selectFirst("div.field-item.odd");
            address += div == null ? "" : div.ownText();
        }

        Element addressDiv = clubPage.selectFirst("div.field.field-type-text.field-field-address");
        if(addressDiv != null) {
            Element div = addressDiv.selectFirst("div.field-item.odd");
            String join = address.isEmpty() ? "" : ", ";
            address += div == null ? "" : (join + div.ownText());
        }
        return address;
    }

    private LocalDateTime parseDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
        try {
            return LocalDateTime.parse(str, formatter);
        }
        catch (Exception e) {
            try {
                formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate date = LocalDate.parse(str, formatter);
                LocalDateTime dateTime = LocalDateTime.now();
                dateTime = dateTime.minusHours(dateTime.getHour())
                        .minusMinutes(dateTime.getMinute())
                        .minusSeconds(dateTime.getSecond())
                        .minusNanos(dateTime.getNano());
                return LocalDateTime.of(date, dateTime.toLocalTime());
            }
            catch (Throwable t) {
                log.error("unexpected exception: ", t);
                parseTournamentsResult.getUnexpectedExceptions().add(t);
            }
        }
        log.warn("Could not parse date: {}", str);
        return null;
    }

}
