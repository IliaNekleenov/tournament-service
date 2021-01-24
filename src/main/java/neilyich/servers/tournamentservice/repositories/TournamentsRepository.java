package neilyich.servers.tournamentservice.repositories;

import neilyich.servers.tournamentservice.model.Tournament;
import neilyich.servers.tournamentservice.model.WebSite;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TournamentsRepository extends PagingAndSortingRepository<Tournament, UUID> {

    Tournament findByNameAndHrefAndWebSite(String name, String href, WebSite webSite);

    List<Tournament> findAllByIdIn(List<UUID> ids);

    List<Tournament> findAllByIdInAndClubCityIn(List<UUID> ids, List<String> cities);
}
