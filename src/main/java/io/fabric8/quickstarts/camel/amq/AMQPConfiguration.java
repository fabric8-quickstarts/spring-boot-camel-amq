package io.fabric8.quickstarts.camel.amq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration parameters filled in from application.properties and overridden using env variables on OpenShift.
 */
@Configuration
@ConfigurationProperties(prefix = "amqp")
public class AMQPConfiguration {

    /**
     * AMQ service name
     */
    private String serviceName;

    /**
     * AMQ parameters
     */
    private String parameters;

    /**
     * AMQ username
     */
    private String username;

    /**
     * AMQ service port
     */
    private String servicePort;

    /**
     * AMQ password
     */
    private String password;

    public AMQPConfiguration() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
