package si.kkobau;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.reactive.messaging.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MatchConsumer {

    private static final Logger LOG = Logger.getLogger(MatchConsumer.class);

    void onStart(@Observes StartupEvent ev) {

    }

    @Incoming("match-topic")
    void processMatch(String match) {
        LOG.info(match);
    }
}
