#Using as a dependency

For configuring, please set the next properties in the application.properties from your project:

```
spring.factmanager.datasource.platform=mysql
spring.factmanager.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.factmanager.datasource.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.factmanager.datasource.jdbc-url=jdbc:mysql://my-server.com:3306/factmanager?useSSL=false&autoReconnect=true
spring.factmanager.datasource.username=factmanager
spring.factmanager.datasource.password=my-password
spring.factmanager.datasource.jpa.hibernate.ddl-auto=update
spring.factmanager.datasource.initialize=true
spring.factmanager.datasource.initialization-mode=always
```

Remember to set as @Primary the beans inside the database configuration from your project.

And add the FormManager configuration file:

Next, include the specific configuration of FactManager in your project with this setting:

```
@ConfigurationPropertiesScan({"com.biit.factmanager.persistence.configuration"})
@EnableJpaRepositories({"com.biit.factmanager.persistence.repositories"})
``` 

And remember, for creating a custom facts that extends `Fact<T>` the child class *must be*
on `com.biit.factmanager.persistence` as must be executed with the `FactManagerDatabaseConfiguration`. If not, Spring
launches a weird error `IllegalArgumentException: Unknown entity`.

### Rest project and Rest Server

As the rest classes are used by the Rest Client are in a specific package without the spring boot repackage. The Rest
module must be deployed as a standard one in Artifactory to be use as a test dependency by Rest Client module.

The Rest Server package, includes all spring boot configuration to be run as a server.

# Using Fact Rest Client

If you want to access to this server, a client is provided to help you. Include on your mvn `pom.xml` file:

```
 <dependency>
       <groupId>com.biit</groupId>
       <artifactId>fact-manager-rest-client</artifactId>
 </dependency>
```

Has a custom logger. Add its configuration to the `logback.xml` file:

```
    <logger name="com.biit.factmanager.logger.FactClientLogger" additivity="false" level="DEBUG">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="DAILY"/>
    </logger>
```

### Custom Fact Provider

Implement the fact with your needs. It must extend the Fact abstract class and implement the IFact interface:

```
@Entity
@DiscriminatorValue("MyFact")
public class MyFact extends Fact<MyFactValue> implements IKafkaStorable, EventPayload {
    [...]
}
```

Include FactClient and SecurityClient to your beans:

```
@ComponentScan("...", "com.biit.factmanager.client", "com.biit.server.client")
```

Implement then a provider for your facts creating a bean

```
@Bean
@Qualifier("myFactProvider")
public FactProvider<MyFact> getMyFactProvider(FactRepository<MyFact> factRepository) {
    return new FactProvider<>(MyFact.class, factRepository);
}
```

This bean is needed, as the JPA discriminator used on the table, needs the class defined above to filter the table. If
not, discriminatoryValue on search must be always `null`.

Latest, generate the API for this:

```
@RequestMapping(value = "/my-facts")
@RestController
public class MyFactServices extends FactServices<MyFactValue, MyFact> {

    public FormrunnerQuestionFactServices(FactProvider<MyFact> factProvider) {
        super(factProvider);
    }
}
```

And all basic services will be generated.

### Settings

Remember to set the properties for connecting the client:

```
facts.customer=<<Who posts the fact>>
factmanager.server.url=http://fact-server
jwt.user=
jwt.password=
```

# Fact Fields

**id** = The database id of the fact.

**organization** = If the fact is related to a specific organization.

**unit** = related to a team, department or any other group of users.

**application** = which application has created the fact. Related to the `ReplyTo` of an event.

**tenant** = for tenancy (not implemented).

**session** = if is related to a set of facts. Related to the `SessionId` of an event.

**subject** = the action done: created, updated, report...

**grouping** = the relating among other facts. Related to the `topic` of an event.

**factType** = the content of the fact. If it is a variable value, an infographic, etc.

**value** = the content of the fact as JSON. Is the payload of an event.

**elementName** = the name of the content. A form Name, a user name, etc.

**element** = the UUID of the fact. Related to the `messageId` of an event.

**customProperties** = a collection of properties that are specifics to a fact. Stored in a different SQL table.

# Database Encryption

If the database encryption is set, assigning any value to the next property:

```
database.encryption.key=<<any value>>
``` 

Search by parameters inside the fact will be disabled, as the content of the fact will be encrypted.
If set, an exception will be launched. 
