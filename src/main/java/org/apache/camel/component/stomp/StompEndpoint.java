package org.apache.camel.component.stomp;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import net.ser1.stomp.Client;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class StompEndpoint extends DefaultEndpoint {

    private static final transient Logger LOG = LoggerFactory.getLogger(StompEndpoint.class);

    private StompConfiguration config;

    private Client client;
    private boolean shouldDisconnect = false;

    public StompEndpoint(String uri, StompComponent component, StompConfiguration config) {
        super(uri, component);
        this.config = config;
    }


    @Override
    public Producer createProducer() throws Exception {
        return new StompProducer(this);
    }


    /**
     * Attempt connect client.
     * Make at most *maxAttempts* while there are errors connecting.
     *
     * @return
     */
    public synchronized Client getClient() {

        long attempts = config.getMaxAttempts();

        while (!shouldDisconnect && isOffline() && attempts > 0) {
            try {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Attempting to connect STOMP Client to: " + config.getHostname() + ":" + config.getPort());
                }
                client = new Client(config.getHostname(), config.getPort(), config.getUsername(), config.getPassword());
                LOG.info("Connected to Stomp.");

                return client;

            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error attempting to connect to STOMP server " + config.getHostname() + ":" + config.getPort() + ": " + e.getMessage());
                }
                //retry
                attempts--;
                if (attempts == 0) {
                    LOG.error("Giving up on connecting to Stomp server.", e);
                    throw new RuntimeCamelException(e);
                }

                int wait = 5000 + ((int) (Math.random() * 30000));
                LOG.warn("Waiting " + wait + "s to retry [" + attempts + " remaining]...");
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e1) {
                }
            } catch (LoginException e) {
                throw new RuntimeCamelException(e);
            }
        }

        return client;
    }

    private boolean isOffline() {
        return client == null || client.isClosed() || !client.isConnected();
    }

    public void disconnect() {
        LOG.debug("Disconnecting...");

        if (!isOffline()) {
            client.disconnect();
        }

        shouldDisconnect = true;
        Thread.currentThread().interrupt();
    }


    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public StompConfiguration getConfig() {
        return config;
    }


}
