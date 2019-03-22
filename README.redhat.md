# Spring Boot, Camel and AMQ Online QuickStart

This quickstart shows how to connect a Spring-Boot application to AMQ Online and use JMS messaging between two Camel routes using OpenShift.

The Red Hat AMQ Online product should already be installed and running on your OpenShift installation, you can follow the [installation guide](https://access.redhat.com/documentation/en-us/red_hat_amq/7.2/html/installing_and_managing_amq_online_on_openshift_container_platform/installing-messaging)

### Configuration

The quickstart must run on a OpenShift project different from the one where AMQ Online is deployed.

Before running the quickstart, you need to configure the `src/main/fabric8/deployment.yml` file in order to 
use the correct remote instance of AMQ Online.
The `AMQP_SERVICE_NAME` environment variable must point to the hostname of the external "messaging" route exposed by AMQ Online.


### Building

The example can be built with

    mvn clean install
    
### Running the example locally

The example can be run locally using the following Maven goal:

    mvn spring-boot:run

### Running the example in OpenShift

It is assumed that:
- OpenShift platform is already running, if not you can find details how to [Install OpenShift at your site](https://docs.openshift.com/container-platform/3.11/install_config/index.html).
- Your system is configured for Fabric8 Maven Workflow, if not you can follow the [Get Started Guide](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.2/html/fuse_on_openshift_guide/fabric8-maven-plugin)
- The Red Hat AMQ Online product should already be installed and running on your OpenShift installation, you can follow the [installation guide](https://access.redhat.com/documentation/en-us/red_hat_amq/7.2/html/installing_and_managing_amq_online_on_openshift_container_platform/installing-messaging)

Then the following command will package your app and run it on OpenShift:

    mvn fabric8:deploy

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

You can also use the openshift [web console](https://docs.openshift.com/container-platform/3.11/getting_started/developers_console.html) to manage the
running pods, and view logs and much more.

### Running via an S2I Application Template

Application templates allow you deploy applications to OpenShift by filling out a form in the OpenShift console that allows you to adjust deployment parameters.  This template uses an S2I source build so that it handle building and deploying the application for you.

First, import the Fuse image streams:

    oc create -f https://raw.githubusercontent.com/jboss-fuse/application-templates/GA/fis-image-streams.json

Then create the quickstart template:

    oc create -f https://raw.githubusercontent.com/jboss-fuse/application-templates/GA/quickstarts/spring-boot-camel-amq-template.json

Now when you use "Add to Project" button in the OpenShift console, you should see a template for this quickstart.
