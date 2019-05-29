# Spring Boot, Camel and EnMasse QuickStart

This quickstart demonstrates how to connect a Spring-Boot application to EnMasse (MaaS) and use JMS messaging between two Camel routes using Kubernetes or OpenShift.

This quickstart requires EnMasse to have been deployed and running first. To install EnMasse into OpenShift or Kubernetes follow [documentation](https://enmasse.io/documentation/master/openshift/#installing-messaging).

### Configuration

The quickstart must run on a Kubernetes/OpenShift project different from the one where EnMasse is deployed.

Log in as a user with cluster-admin privileges:

 ```
 oc login -u system:admin
 ```

Before running the quickstart, you need to configure EnMasse (create user and queue). Run the following commands to create new project and apply configuration files:

 ```
 oc new-project MY_PROJECT_NAME
 oc apply -f src/main/resources/k8s
 ```

### Building

The example can be built with

 ```
 mvn clean install
 ```

### Running the example locally

Before running the quickstart, you need to configure the `src/main/fabric8/deployment.yml` file in order to
use the correct remote instance of AMQ EnMasse.


Get remote url of EnMasse instance by running following command:

 ```
 oc get addressspace spring-boot-camel-amq -o jsonpath={.status.endpointStatuses[?(@.name==\'messaging\')].externalHost}
 ```

Fill this value into `src/main/resources/application.properties` instead of `FILL_ME`.

The example can be then run locally using the following Maven goal:

 ```
 mvn spring-boot:run
 ```

### Running the example in Kubernetes/OpenShift

It is assumed a running Kubernetes platform is already running. If not you can find details how to [get started](http://fabric8.io/guide/getStarted/index.html).

Assuming your current shell is connected to Kubernetes or OpenShift so that you can type a command like

```
kubectl get pods
```

or for OpenShift

```
oc get pods
```

Then the following command will package your app and run it on Kubernetes:

```
mvn fabric8:run
```

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

You can also use the [fabric8 developer console](http://fabric8.io/guide/console.html) to manage the running pods, and view logs and much more.

### More details

You can find more details about running this [quickstart](http://fabric8.io/guide/quickstarts/running.html) on the website. This also includes instructions how to change the Docker image user and registry.

