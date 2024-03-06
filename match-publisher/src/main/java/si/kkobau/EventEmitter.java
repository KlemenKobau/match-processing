package si.kkobau;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@ApplicationScoped
public class EventEmitter {

    private static final Logger LOG = Logger.getLogger(EventEmitter.class);

    @Channel("match-topic")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER)
    private Emitter<String> matchEmitter;

    @ConfigProperty(name = "matchfile.location")
    private String matchFileLocation;

    void onStart(@Observes StartupEvent ev) {
        try(InputStream matchStream = getMatchStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(matchStream))) {

            while (reader.ready()) {
                String line = reader.readLine();
                sendLineToKafka(line);
            }

        } catch (IOException e) {
            LOG.error("Error handling file", e);
            throw new RuntimeException(e);
        }
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

    private void sendLineToKafka(String line) {
        matchEmitter.send(line);
    }
}
