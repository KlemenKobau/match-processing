package si.kkobau;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Stream;

@ApplicationScoped
public class EventProcessor {

    private static final Logger LOG = Logger.getLogger(EventProcessor.class);

    @ConfigProperty(name = "matchfile.location")
    private String matchFileLocation;

    @Startup
    public void onStart() {
        LOG.info("START");
        saveMatches();
        LOG.info("END");
    }

//    Just save everything in a single transaction ~1min
    @Transactional
    public void saveMatches() {
        BufferedReader matchStream = getMatchStream();
        Stream<Match> matches = matchStream.lines().skip(1).map(this::createMatch);

        Match.persist(matches);
    }

    private BufferedReader getMatchStream() {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(matchFileLocation);

        if (ioStream == null) {
            throw new IllegalArgumentException(matchFileLocation + " is not found");
        }
        return new BufferedReader(new InputStreamReader(ioStream));
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
