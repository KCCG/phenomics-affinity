```
 ______   _                                  _                 _______     ___     ___  _         _
(_____ \ | |                                (_)               (_______)   / __)   / __)(_)       (_)   _
 _____) )| |__   _____  ____    ___   ____   _   ____   ___    _______  _| |__  _| |__  _  ____   _  _| |_  _   _
|  ____/ |  _ \ | ___ ||  _ \  / _ \ |    \ | | / ___) /___)  |  ___  |(_   __)(_   __)| ||  _ \ | |(_   _)| | | |
| |      | | | || ____|| | | || |_| || | | || |( (___ |___ |  | |   | |  | |     | |   | || | | || |  | |_ | |_| |
|_|      |_| |_||_____)|_| |_| \___/ |_|_|_||_| \____)(___/   |_|   |_|  |_|     |_|   |_||_| |_||_|   \__) \__  |
                                                                                                           (____/
```
Introduction
============
Phenmomic-Affinity is a Java8 SpringBoot Application powered by Luwak and Lucene to identify affinities in biomedical text.

Following are key components for this service:

**Engine**
* Generate lucene reverse indices based on curated queries for different ontologies
* Provide an interface for a text string to be passed through index and return:
    * String Ids of queries -> Directly mapped to ontology ID
    * Offsets of lucene hits
* Currently there is not processing is performed on the input text.
* Queries and text are case independent

**Index handling**
* Mondo: Ontology is parsed by index generator and as Json is created.

**Interfaces**
Restful APIs.

***APIs***
A few notable points:
* There is only one controller (`AnnotationController`)

Setup
=====
1. The build tool is gradle to keep dependencies clean and build process simple.
2. To run the application locally `./gradlew bootRun` can be used.
3. Application uses `port 9020` (All phenomics microservices are following 90*0 pattern)

Deployment
==========
1. DevOps code contains script to build docker image, tag it and publish it to ECR.
2. DevOps requires AWS CLI and credentials in environment variables.


Install in Intellij
===================
This project uses Lombok and you need to install the following to get it to work:
* Lombok plugin
* Enable Annotation processing by going to:
Preferences -> Build, execution, deployment, compiler, Annotation Processors
Creating a profile for the project and enabling it

https://www.jetbrains.com/help/idea/2016.1/configuring-annotation-processing.html

Clean & Rebuild and then you should be good to go.

