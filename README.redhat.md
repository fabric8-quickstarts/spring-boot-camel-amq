# Spring Boot, Camel and ActiveMQ QuickStart

This quickstart demonstrates how to connect a Spring-Boot application to an ActiveMQ broker and use JMS messaging between two Camel routes using OpenShift.

In this example we will use two containers, one container to run as a ActiveMQ broker, and another as a client to the broker, where the Camel routes is running.

This quickstart requires the ActiveMQ broker has been deployed and running first. This can be done from the web console from the `Apps` page, and then install the `messaging` application.

### Building

The example can be built with

    mvn clean install


### Running the example locally

The example can be run locally using the following Maven goal:

    mvn spring-boot:run


### Running the example in OpenShift

It is assumed a running OpenShift platform is already running. 

Assuming your current shell is connected to OpenShift so that you can type a command like

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


