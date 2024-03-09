package si.kkobau;

import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

@ApplicationScoped
public class EventEmitter {

    private static final Logger LOG = Logger.getLogger(EventEmitter.class);

    @ConfigProperty(name = "matchfile.location")
    private String matchFileLocation;

    @Outgoing("match-topic")
    @OnOverflow(OnOverflow.Strategy.BUFFER)
    public Multi<Record<String, String>> generate() {
        LOG.info("Started sending");
        BufferedReader reader = new BufferedReader(new InputStreamReader(getMatchStream()));
        Stream<Record<String, String>> itemStream = reader.lines().skip(1).map(this::lineToRecord)
                .onClose(() -> LOG.info("Stopped sending"));

        return Multi.createFrom().items(itemStream);
    }

    private InputStream getMatchStream() {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(matchFileLocation);

        if (ioStream == null) {
            throw new IllegalArgumentException(matchFileLocation + " is not found");
        }
        return ioStream;
    }

    private Record<String, String> lineToRecord(String line) {
        String key = line.split("\\|")[0];
        return Record.of(key, line);
    }
}
