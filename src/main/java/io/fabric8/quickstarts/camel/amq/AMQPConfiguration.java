package io.fabric8.quickstarts.camel.amq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration parameters filled in from application.properties and overridden using env variables on Openshift.
 */
@Configuration
@ConfigurationProperties(prefix = "amq-camel")
public class AMQPConfiguration {

    /**
     * AMQ service name
     */
    private String serviceName;

    /**
     * AMQ service port
     */
    private Integer port;

    /**
     * AMQ username
     */
    private String username;

    /**
     * AMQ password
     */
    private String password;

    /**
     * AMQ service port AMQP
     */
    @Value("${AMQ_CAMEL_SERVICE_PORT_AMQP}")
    private String servicePortAMQP;

    /**
     * AMQ service port AMQPS
     */
    @Value("${AMQ_CAMEL_SERVICE_PORT_AMQPS}")
    private String servicePortAMQPS;

    /**
     * AMQ parameters
     */
    private String parameters;

    /**
     * AMQ protocol (amqp or amqps)
     */
    private String protocol;

    public AMQPConfiguration() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String servicename) {
        this.serviceName = servicename;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getServicePort_amqp() {
        return servicePortAMQP;
    }

    public void setServicePortAMQP(String servicePortAMQP) {
        this.servicePortAMQPS = servicePortAMQP;
    }

    public String getServicePort_amqps() {
        return servicePortAMQPS;
    }

    public void setServicePortAMQPS(String servicePortAMQPS) {
        this.servicePortAMQP = servicePortAMQPS;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    @Override
    public String toString() {
        return "AMQPConfiguration{" +
                "serviceName='" + serviceName + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", servicePortAmqp='" + servicePortAMQP + '\'' +
                ", servicePortAmqps='" + servicePortAMQPS + '\'' +
                ", parameters='" + parameters + '\'' +
                ", protocol='" + protocol + '\'' +
                '}';
    }
}
