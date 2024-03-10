package si.kkobau;

import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@ApplicationScoped
public class MatchValidationService {

    private static final Logger LOG = Logger.getLogger(MatchValidationService.class);

    @ConfigProperty(name = "matchfile.location")
    private String matchFileLocation;

    @Startup
    void onStartup() {
        LOG.info("Started validating.");
        Iterator<Match> sortedMatches = getSortedMatches().iterator();
        Iterator<Match> sortedGroundTruth = getSortedLineStream().iterator();

        LOG.info("Created data streams.");

        long currentMatch = 0;

        while (sortedMatches.hasNext() && sortedGroundTruth.hasNext()) {
            Match truth = sortedGroundTruth.next();
            Match actual = sortedMatches.next();

            if (currentMatch % 10000 == 0) {
                LOG.info("Processing match: %d".formatted(currentMatch));
            }

            if (!areMatchesEqual(truth, actual)) {
                LOG.error(truth);
                LOG.error(actual);
                throw new RuntimeException("Not equal");
            }

            currentMatch += 1;
        }

        if (sortedMatches.hasNext() || sortedGroundTruth.hasNext()) {
            throw new RuntimeException("Different amount of matches");
        }

        LOG.info("Successfully validated all matches.");
        Quarkus.asyncExit();
    }

    private boolean areMatchesEqual(Match one, Match other) {
        return Objects.equals(one.getMatchId(), other.getMatchId())
                && Objects.equals(one.getMarketId(), other.getMarketId())
                && Objects.equals(one.getOutcomeId(), other.getOutcomeId())
                && Objects.equals(one.getSpecifiers(), other.getSpecifiers());
    }

    private Stream<Match> getSortedMatches() {
        Sort matchSort = Sort.ascending("matchId", "createdAt");
        List<Match> matchList = Match.listAll(matchSort);

        return matchList.stream();
    }

    private Stream<Match> getSortedLineStream() {

        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(matchFileLocation);

        if (ioStream == null) {
            throw new IllegalArgumentException(matchFileLocation + " is not found");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(ioStream));
        return reader.lines()
                .skip(1)
                .map(this::createMatch)
                .sorted(Comparator.comparing(Match::getMatchId));
    }

    public Match createMatch(String matchString) {
        String[] split = matchString.split("\\|", 4);
        String matchId = split[0].substring(1, split[0].length() - 1);
        long marketId = Long.parseLong(split[1]);
        String outcomeId = split[2].substring(1, split[2].length() - 1);
        String specifiers = split[3];

        if (!specifiers.isBlank()) {
            specifiers = specifiers.substring(1, specifiers.length() - 1);
        }

        Match match = new Match();
        match.setMatchId(matchId);
        match.setMarketId(marketId);
        match.setOutcomeId(outcomeId);
        match.setSpecifiers(specifiers);

        return match;
    }
}
