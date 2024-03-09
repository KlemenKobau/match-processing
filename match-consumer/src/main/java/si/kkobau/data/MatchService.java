package si.kkobau.data;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MatchService {

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
