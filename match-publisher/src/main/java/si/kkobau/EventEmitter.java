package si.kkobau;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Collections;
import java.util.Properties;

@ApplicationScoped
public class EventEmitter {

    @ConfigProperty(name = "kafka.bootstrap.servers")
    private String bootstrapServers;

    private Admin kafkaAdmin;

    private void onStart(@Observes StartupEvent ev) {
        Properties properties = new Properties();
        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
        );

        this.kafkaAdmin = Admin.create(properties);
    }

    private void onStop(@Observes ShutdownEvent ev) {
        kafkaAdmin.close();
    }

    private void createTopic(String topicName) {
        int partitions = 1;
        short replicationFactor = 1;
        NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

        kafkaAdmin.createTopics(
                Collections.singleton(newTopic)
        );
    }
}
