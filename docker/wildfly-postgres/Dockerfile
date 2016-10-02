FROM jboss/wildfly:10.1.0.Final

ENV POSTGRESQL_JDBC_DRIVER_VERSION 9.4-1206-jdbc42

RUN curl -o /opt/jboss/postgresql-${POSTGRESQL_JDBC_DRIVER_VERSION}-bin.jar "http://central.maven.org/maven2/org/postgresql/postgresql/${POSTGRESQL_JDBC_DRIVER_VERSION}/postgresql-${POSTGRESQL_JDBC_DRIVER_VERSION}.jar"

ADD datasource.cli /opt/jboss/datasource.cli
ADD awesomeLogging.cli /opt/jboss/awesomeLogging.cli

RUN sed -i -e s/POSTGRESQL-JDBC-DRIVER-VERSION/${POSTGRESQL_JDBC_DRIVER_VERSION}/g /opt/jboss/datasource.cli

RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/jboss/datasource.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/jboss/awesomeLogging.cli
 
RUN rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/current/*
RUN chown -R jboss:jboss /opt/jboss/wildfly/standalone

RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent

ENTRYPOINT ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug", "8787"]
