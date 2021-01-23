package neilyich.servers.tournamentservice.services.filters;

import lombok.AllArgsConstructor;
import neilyich.servers.tournamentservice.model.Category;
import neilyich.servers.tournamentservice.model.Tournament;

import java.util.Set;

@AllArgsConstructor
public class CategoryFilter implements TournamentFilter {
    private final Set<Category> categories;
    @Override
    public boolean isSuitable(Tournament tournament) {
        if(categories.contains(Category.ALL)) {
            return true;
        }
        return categories.contains(tournament.getCategory());
    }
}
