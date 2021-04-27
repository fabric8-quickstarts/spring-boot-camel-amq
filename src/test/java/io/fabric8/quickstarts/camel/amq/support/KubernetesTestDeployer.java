package io.fabric8.quickstarts.camel.amq.support;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
import io.fabric8.kubernetes.api.model.rbac.RoleBindingBuilder;
import io.fabric8.kubernetes.api.model.rbac.Subject;
import io.fabric8.kubernetes.api.model.rbac.SubjectBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.openshift.api.model.ImageLookupPolicy;
import io.fabric8.openshift.api.model.ImageStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.fabric8.quickstarts.camel.amq.support.KubernetesTestUtil.failNotDeployed;
import static io.fabric8.quickstarts.camel.amq.support.KubernetesTestUtil.isNotNullOrEmpty;
import static io.fabric8.quickstarts.camel.amq.support.KubernetesTestUtil.isNullOrEmpty;
import static java.util.concurrent.TimeUnit.SECONDS;

public class KubernetesTestDeployer {

    private final static Logger LOG = LoggerFactory.getLogger(KubernetesTestDeployer.class);

    private final static Predicate<HasMetadata> supportReadiness =
            resource -> Arrays.asList("Node", "Deployment", "ReplicaSet", "StatefulSet", "Pod", "DeploymentConfig", "ReplicationController")
                    .contains(resource.getKind());

    public static void deploy(KubernetesClient client, KubernetesTestConfig config) {
        createNamespace(client, config);

        // deploy dependencies
        if (isNotNullOrEmpty(config.getDependencies())) {
            LOG.info("Deploy dependencies from file {}", config.getImageStreamFilePath());
            for (String dependency : config.getDependencies()) {
                deployFromFile(client, config, dependency);
            }
        }

        String sourceNamespace = config.getMainNamespace();
        LOG.info("Deploy RoleBinding ");
        createRoleBinding(client, sourceNamespace, config);
        LOG.info("Deploy ImageStreams from file {}", config.getImageStreamFilePath());
        createImageStream(client, config);
        // deploy resources
        LOG.info("Deploy resources from file {}", config.getResourceFilePath());
        deployFromFile(client, config, config.getResourceFilePath());
    }


    private static void deployFromFile(KubernetesClient client, KubernetesTestConfig config, String resourcesFilePath) {
        if (isNullOrEmpty(resourcesFilePath)) {
            failNotDeployed();
        }
        LOG.info("Loading resources file: " + resourcesFilePath);
        List<HasMetadata> resourceList = null;

        try (InputStream resources = new FileInputStream(resourcesFilePath)) {
            resourceList = client.load(resources).get();
        } catch (IOException e) {
            LOG.error("Problem loading resources file {}", resourcesFilePath);
            failNotDeployed();
        }

        List<HasMetadata> deployedResourceList = client.resourceList(resourceList)
                .inNamespace(config.getNamespace())
                .createOrReplace();

        LOG.info("Waiting for reources to be ready");
        deployedResourceList.stream()
                .filter(supportReadiness)
                .forEach(resource -> {
                    try {
                        client.resource(resource)
                                .inNamespace(config.getNamespace())
                                .waitUntilReady(config.getKubernetesTimeout(), SECONDS);
                    } catch (InterruptedException e) {
                        LOG.error("Timeout reached waiting for "+resource.getKind()+" with name "+resource.getMetadata().getName()+" to be ready");
                        failNotDeployed();
                    }
                });
        LOG.info("Reources are ready Now");
    }

    private static void createRoleBinding(KubernetesClient client, String sourceNamespace, KubernetesTestConfig config) {

        String targetNamespace = config.getNamespace();

        Subject subject = new SubjectBuilder()
                .withName("default")
                .withKind("ServiceAccount")
                .withNamespace(targetNamespace)
                .build();

        RoleBindingBuilder roleBindingBuilder = new RoleBindingBuilder();
        RoleBinding roleBinding = roleBindingBuilder
                .withKind("RoleBinding")
                .withApiVersion("rbac.authorization.k8s.io/v1")
                .withNewMetadata()
                .withName("ktest-system:image-puller")
                .withNamespace(sourceNamespace)
                .withLabels(config.getKtestLabels())
                .endMetadata()
                .withSubjects(subject)
                .withNewRoleRef()
                .withApiGroup("rbac.authorization.k8s.io")
                .withKind("ClusterRole")
                .withName("system:image-puller")
                .endRoleRef()
                .build();

        client.rbac().roleBindings().inNamespace(sourceNamespace).createOrReplace(roleBinding);
    }

    private static void createNamespace(KubernetesClient client, KubernetesTestConfig config) {
        if (config.isUseExistingNamespace()) {
            return;
        }
        Namespace ns = new NamespaceBuilder().withNewMetadata().withName(config.getNamespace()).endMetadata().build();
        client.namespaces().create(ns);
        LOG.info("Namespace " + config.getNamespace() + " created.");
    }


    public static void deleteNamespace(KubernetesClient
                                               client, KubernetesTestConfig config) {
        final CountDownLatch isWatchClosed = new CountDownLatch(1);
        Watch watch = client.namespaces().withName(config.getNamespace()).watch(new Watcher<Namespace>() {
            @Override
            public void eventReceived(Action action, Namespace resource) {
                if (action.equals(Action.DELETED)) {
                    LOG.debug("Deleted event for namespace {} received", config.getNamespace());
                    isWatchClosed.countDown();
                }
            }
            @Override
            public void onClose(KubernetesClientException cause) {
                isWatchClosed.countDown();
            }
        });
        try {
            if (config.isShouldDestroyNamespace()) {
                client.namespaces().withName(config.getNamespace()).delete();
                LOG.info("Waiting for namespace deletion " + config.getNamespace() + " ...");
                isWatchClosed.await(config.getKubernetesTimeout(), TimeUnit.SECONDS);
                LOG.info("Namespace - " + config.getNamespace() + " deleted.");
            }
        } catch (InterruptedException e) {
            isWatchClosed.countDown();
            watch.close();
            throw new RuntimeException("Timeout reached while waiting for namespace deletion.");
        }
    }

    private static void createImageStream(KubernetesClient client, KubernetesTestConfig config) {
        try (InputStream imageStreamFile = new FileInputStream(config.getImageStreamFilePath())) {
            List<HasMetadata> result = client.load(imageStreamFile).get();
            result = result.stream().peek(
                    x -> {
                        if (x instanceof ImageStream) {
                            ((ImageStream) x).getSpec()
                                    .setLookupPolicy(new ImageLookupPolicy(true));
                        }
                    }
            ).collect(Collectors.toList());

            client.resourceList(result)
                    .inNamespace(config.getNamespace())
                    .createOrReplace();
        } catch (IOException e) {
            failNotDeployed();
        }
    }
}
