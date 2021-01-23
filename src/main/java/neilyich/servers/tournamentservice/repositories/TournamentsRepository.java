package neilyich.servers.tournamentservice.repositories;

import neilyich.servers.tournamentservice.model.Tournament;
import neilyich.servers.tournamentservice.model.WebSite;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentsRepository extends PagingAndSortingRepository<Tournament, Integer> {

    Tournament findByNameAndHrefAndWebSite(String name, String href, WebSite webSite);

    List<Tournament> findAllByIdIn(List<Integer> ids);

}
