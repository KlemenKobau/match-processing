package si.kkobau;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.*;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import si.kkobau.data.Match;
import si.kkobau.data.MatchService;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MatchConsumer {

    private static final Logger LOG = Logger.getLogger(MatchConsumer.class);
    private final MatchService matchService;

    @Inject
    public MatchConsumer(MatchService matchService) {
        this.matchService = matchService;
    }

    @Incoming("match-topic")
    @WithTransaction
    Uni<Void> processMatch(List<String> matchInput) {
        List<Match> matches = new ArrayList<>();
        for (String matchString : matchInput) {
            Match match = matchService.createMatch(matchString);
            matches.add(match);
        }

        return Match.persist(matches);
    }
}
