FROM jboss/keycloak:latest

USER root

RUN yum update -y && yum install -y wget && yum clean all

WORKDIR /opt/jboss/keycloak/standalone/deployments

# run pwd

RUN wget -O keycloak-cas-services-4.8.3.Final.jar https://search.maven.org/remotecontent?filepath=io/github/johnjcool/keycloak-cas-services/4.8.3.Final/keycloak-cas-services-4.8.3.Final.jar

# run ping localhost

USER 1000

EXPOSE 8080

ENTRYPOINT [ "/opt/jboss/tools/docker-entrypoint.sh" ]

CMD ["-b", "0.0.0.0"]

# # docker build -t keycloak-cas:keycloak-5.0.0-cas docker/keycloak-cas
