This project is intended to be a Java EE zen garden utilizing modern Java EE best practices and strives for minimalism, efficiency, and above all, developer experience.

TODO:

- Configure all tests to run in CI with Maven
- Streamline local dev even more, switch to IntelliJ, should clone project and click run/debug
- Add Mockito for unit level testing
- Switch to h2 or some embedded db for local dev
- Set up flyway and targets in the ide/maven for seeding/resetting the DB for local dev.

Branch Wildfly18
- Update to Java 11 and Wildfly 18.0.1
- Update to Junit 5

Branch Wildfly26
- Update to Java 19/20 and Wildfly 26

Branch main
- Update to Java, JakartaEE, and Wildfly @latest.

Branch next
- Use microprofile and minimize fatness of Wildfly somehow

Currently local dev is done with docker-compose.  Compose up and attach to the app server with an IDE and publish on changes.

Initialize

`docker-compose build`

Start Docker

`docker-compose up -d`

Shutdown Docker

`docker-compose down`
