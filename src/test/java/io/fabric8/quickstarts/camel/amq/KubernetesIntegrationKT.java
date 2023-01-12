/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package io.fabric8.quickstarts.camel.amq;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.quickstarts.camel.amq.support.KubernetesTestConfig;
import io.fabric8.quickstarts.camel.amq.support.KubernetesTestSetup;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static io.fabric8.quickstarts.camel.amq.support.KubernetesTestConfig.createConfig;

public class KubernetesIntegrationKT {

    private static Logger LOG = LoggerFactory.getLogger(KubernetesIntegrationKT.class);


    @Test
    public void testAppProvisionsRunningPods() {
        KubernetesTestConfig config = createConfig();
        KubernetesTestSetup testSetup = new KubernetesTestSetup(config);

        try {
            testSetup.setUp();
            KubernetesClient client = config.getClient();
            PodList podList = client.pods().inNamespace(config.getNamespace()).list();

            if (podList.getItems().isEmpty()) {
                Assert.fail("No pods found in namespace "+ config.getNamespace());
            }

            for (Pod pod : podList.getItems()) {
                try {
                    if (!pod.getMetadata().getName().endsWith("build") && !pod.getMetadata().getName().endsWith("deploy")) {
                        client.resource(pod)
                                .inNamespace(config.getNamespace())
                                .waitUntilReady(config.getKubernetesTimeout(), TimeUnit.SECONDS);
                        LOG.info("Pod {} is in state: {}",pod.getMetadata().getName(),pod.getStatus().getPhase());
                    }
                } catch (InterruptedException e) {
                    Assert.fail("Timeout reached waiting for pod " + pod.getMetadata().getName());
                }
            }
        } finally {
            testSetup.tearDown();
        }
    }
}
