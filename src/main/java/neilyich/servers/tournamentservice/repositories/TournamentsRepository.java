package neilyich.servers.tournamentservice.repositories;

import neilyich.servers.tournamentservice.model.Tournament;
import neilyich.servers.tournamentservice.model.WebSite;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TournamentsRepository extends PagingAndSortingRepository<Tournament, Integer> {

    Tournament findByNameAndHrefAndWebSite(String name, String href, WebSite webSite);

    List<Tournament> findAllByIdIn(List<Integer> ids);

//    @Query(value = "select * from tournaments t where " +
//            "t.id in (select e.tournament_id from events e where (e.event_name='START_TOURNAMENT' and (e.date between ?1 and ?2)) order by e.date asc)" +
//            " and (?3 is null or t.billiard_type = ?3)" +
//            " and (?4 is null or t.is_free = ?4)" +
//            " and (?5 is null or t.category = ?5)" +
//            " and (?6 is null or t.with_handicap = ?6)" +
//            " and (?7 is null or t.id > ?7);", nativeQuery = true)
//    List<Tournament> findByStartDateInPeriod(LocalDateTime startPeriod, LocalDateTime endPeriod,
//                                             String billiardType, Boolean isFree, String category,
//                                             Boolean withHandicap, Integer tournamentIdFrom);


}
