package neilyich.servers.tournamentservice.repositories;

import neilyich.servers.tournamentservice.model.Club;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClubsRepository extends PagingAndSortingRepository<Club, UUID> {
    Club findByHref(String href);
    Club findByPlace(String place);
}
