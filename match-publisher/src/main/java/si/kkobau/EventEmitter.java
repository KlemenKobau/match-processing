package si.kkobau;

import io.quarkus.runtime.StartupEvent;

import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.apache.kafka.common.protocol.types.Field;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.Record;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@ApplicationScoped
public class EventEmitter {

    private static final Logger LOG = Logger.getLogger(EventEmitter.class);

    @ConfigProperty(name = "matchfile.location")
    private String matchFileLocation;

    @Outgoing("match-topic")
    public Multi<Record<String, String>> generate() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getMatchStream()));
        return Multi.createFrom().items(reader.lines().map(this::lineToRecord));
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
