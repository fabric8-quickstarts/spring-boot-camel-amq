package io.fabric8.quickstarts.camel.amq.support;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.fabric8.quickstarts.camel.amq.support.KubernetesTestDeployer.deleteNamespace;
import static io.fabric8.quickstarts.camel.amq.support.KubernetesTestDeployer.deploy;


public class KubernetesTestSetup {

    protected static Logger LOG = LoggerFactory.getLogger(KubernetesTestSetup.class);

    private KubernetesTestConfig config;

    private KubernetesClient client;

    public KubernetesTestSetup(KubernetesTestConfig config) {
        this.config = config;
        this.client = config.getClient();
    }

    public void setUp() {
        LOG.info("Doing setup...");
        deploy(client, config);
        LOG.info("setup done.");
    }

    public void tearDown() {
        LOG.info("Doing teardown...");
        if(config.isShouldDestroyNamespace()) {
            deleteNamespace(client, config);
            client.rbac()
                    .roleBindings()
                    .inNamespace(config.getMainNamespace())
                    .withLabels(config.getKtestLabels())
                    .delete();
        }else{
            LOG.info("Nothing to do!");
        }
        LOG.info("Teardown done.");
    }
}
