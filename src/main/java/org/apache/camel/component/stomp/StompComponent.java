package org.apache.camel.component.stomp;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultComponent;

/**
 *
 */
public class StompComponent extends DefaultComponent {


    private StompConfiguration config;

    public StompComponent() {
        this.config = new StompConfiguration();
    }

    public StompComponent(StompConfiguration configuration) {
        this.config = configuration;
    }

    public StompComponent(CamelContext context) {
        super(context);
        this.config = new StompConfiguration();
    }

    @Override
    protected StompEndpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        StompConfiguration config = this.config.copy();
        config.configure(uri);

        StompEndpoint endpoint = new StompEndpoint(uri, this, config);
        setProperties(config, parameters);
        return endpoint;
    }


    public void setConfig(StompConfiguration config) {
        this.config = config;
    }


}
