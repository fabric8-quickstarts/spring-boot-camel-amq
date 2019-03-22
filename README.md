# Spring Boot, Camel and EnMasse QuickStart

This quickstart demonstrates how to connect a Spring-Boot application to EnMasse (MaaS) and use JMS messaging between two Camel routes using Kubernetes or OpenShift.

This quickstart requires EnMasse to have been deployed and running first. Follow the directions at

http://enmasse.io/documentation

to install EnMasse into OpenShift or Kubernetes. There is a `deploy.sh` script that will help you install EnMasse. Once EnMasse is installed, please create an incomingOrders queue using the EnMasse console.

### Configuration

The quickstart must run on a Kubernetes/OpenShift project different from the one where EnMasse is deployed.

Before running the quickstart, you need to configure the `src/main/fabric8/deployment.yml` file in order to 
use the correct remote instance of AMQ EnMasse.
The `AMQP_SERVICE_NAME` environment variable must point to the hostname of the external "messaging" route exposed by EnMasse.

### Building

The example can be built with

    mvn clean install


### Running the example locally

The example can be run locally using the following Maven goal:

    mvn spring-boot:run


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

