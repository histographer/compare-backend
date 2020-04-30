# Contributing
This file contains information that you should familiarize yourself with before you start making changes to this repository. At the time of writing, the original development team's time with this project is nearing its end. This file is therefore primarily intended to help a potential future team get started with development, rather than to help outsiders make contributions.

See also [README.md](README.md).

## Technical stuff you should know
### Languages, frameworks, and other technologies
This repository uses Java's *servlet* technology. The next subsection contains some information about how to use servlets. If you want more information, you may find what you're looking for by googling something along the lines of "servlet tutorial". The library documentation for the packages [javax.servlet](https://docs.oracle.com/javaee/7/api/javax/servlet/package-summary.html) and [javax.servlet.http](https://docs.oracle.com/javaee/7/api/javax/servlet/http/package-summary.html) may also prove useful.

We use [MongoDB](https://www.mongodb.com/) as our database system.

The application runs within a Docker container. Some familiarity with [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) is likely to be useful, but is not required if you just want to start writing some code.

Finally, this repository uses [Apache Maven](https://maven.apache.org/) for things like managing dependencies, compiling code, and running tests. If you find that you need to know more about how Maven works, the provided link should have some useful information.

### How the code in this repository works, including some examples
#### Interacting with the database
We use the Data Access Object (DAO) design pattern when interacting with the database. Within the application, data is represented by data models in the `no.digipat.compares.models` package and other packages starting with the same prefix. A model class typically just contains getters and setters for its properties, but may also override methods such as `toString()`, or `equals(Object)` and `hashCode()`. For example:

```java
package no.digipat.compare.models;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Foo {
    
    private Integer id;
    private String name;
    
    public Foo setId(Integer id) {
        this.id = id;
        return this;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public Foo setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
}

```

There are a few things to note here. First of all, the setters return the instance on which they are called. This is useful for chaining setter calls in the following way:

```java
Foo myFoo = new Foo()
        .setId(1)
        .setName("name");

```

Second of all, notice the `EqualsAndHashCode` annotation from [Project Lombok](https://projectlombok.org/). At compile time, this will automatically generate implementations of `equals(Object)` and `hashCode()` based on the class' fields. (See the provided link if you want more information about how equality is determined.) While overriding these methods is not necessary, it can be quite useful, in part because it can turn test code from this:

```java
assertEquals(expectedFoo.getId(), actualFoo.getId());
assertEquals(expectedFoo.getName(), actualFoo.getName());

```

Into this:

```java
assertEquals(expectedFoo, actualFoo);

```

Finally, notice that the id field is declared to be an `Integer`, not the primitive `int`. This is because `0`, which is the default value for primitive ints, does not necessarily make sense as a default value for a model property. The default value `null` of the boxed type is usually a better default in cases like this one.

Once we have a `Foo` model, we can make a DAO class for it, which is responsible for performing `Foo`-related database actions:

```java
package no.digipat.compare.mongodb.dao;

// Various imports go here...

public class MongoFooDAO {
    
    public MongoFooDAO(MongoClient client, String databaseName) { ... }
    
    public void createFoo(Foo foo) { ... }
    
    public Foo getFoo(int id) { ... }
    
}

```

It's worth noting that, although our hypothetical `Foo` model uses an `Integer` field for its ID, the `getFoo` method takes a primitive `int` as an argument. This is because it usually does not make sense for an existing object in the database to have a `null` ID, so there is no point in letting the method accept a `null` value.

If you want to know how to actually implement such a DAO class, you can check out [the existing DAO classes](src/main/java/no/digipat/compare/mongodb/dao).

#### Servlets
A servlet can be thought of as an endpoint. Suppose we want to add the endpoint `GET /foo?id=[id]`. We can do this by creating a class like the following one (preferably in the package `no.digipat.compare.servlets`):

```java
@WebServlet("/foo")
public class FooServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid or missing ID"); // Bad request
            return;
        }
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoFooDAO fooDao = new MongoFooDAO(client, databaseName);
        Foo foo = fooDao.getFoo(id);
        if (foo == null) {
            response.sendError(404, "Couldn't find a foo with ID " + id);
        } else {
            response.getWriter().print("Foo name: " + foo.getName());
        }
    }
    
}

```

The class `FooServlet` is mapped to the path `/foo` by the annotation `WebServlet`. This can also be configured in the [deployment descriptor](src/main/webapp/WEB-INF/web.xml). The method `doGet` handles GET requests (hence the name); if we wanted to, we could also make this servlet handle other HTTP methods, such as POST. We first extract the query parameter `id`. We then use the servlet context to find what we need to connect to the database. (Note that the context attributes `MONGO_CLIENT` and `MONGO_DATABASE` are specific to this application.) We use this information to create a DAO object, which we then use to retrieve the data that the user is requesting. If the data exists, the response body will contain the following text:

```
Foo name: [name of the foo]
```

#### Notes on character encoding
When reading text from a request body, there is a possibility of the input becoming garbled. The application's settings take some measures to prevent this, but in order to make sure that text is decoded correctly (regardless of its encoding), we recommend reading text from request bodies in the following manner:

```java
IOUtils.toString(request.getInputStream(), request.getCharacterEncoding())

```

In addition, it's a good idea to always use some non-ASCII characters (e.g. "æøåαβγ") in integration tests whenever it makes sense to do so, both when sending data (e.g. in the request body) and when retrieving data (e.g. an object that's stored in the database).

## Testing
The automated tests use [JUnit 4](https://junit.org/junit4), and are located in the directory `src/test`. When adding new tests, it is important to make sure that they are recognized by Maven. Every new test class must therefore either be registered with an existing test suite class (such as the suite IntegrationTests for integration tests), or be added to the configuration of the relevant Maven plugin (Surefire for unit tests, Failsafe for integration tests) in [pom.xml](pom.xml). 

The integration tests work by using the [HttpUnit](https://github.com/russgold/httpunit) library to send HTTP requests to a running instance of the application. An issue with HttpUnit is that receiving a response with status code 400 will sometimes cause an exception to be thrown, even if throwing exceptions on error codes has been disabled. If you encounter this problem, you can work around it by sending the affected request by different means, e.g. by using the ["fluent" API](https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html) from the Apache HttpComponents library.

For information about how to run the tests, see README.md.

## Documentation
The classes comprising the application's internal API (such as data models and DAO classes) should be documented with Javadoc comments. See [How to Write Doc Comments for the Javadoc Tool](https://www.oracle.com/technetwork/java/javase/documentation/index-137868.html).

The external web API is documented in [API.md](API.md), so please make sure that you update that file whenever you make changes to the API. It would be preferable to automatically generate documentation from something like source code comments or annotations, using a tool such as [Enunciate](https://enunciate.webcohesion.com/) or [Swagger](https://swagger.io/), but at the time of writing, we have not been able to make this work. If you want to add automatic documentation generation to this project, you could use [this example](https://github.com/swagger-api/swagger-samples/tree/master/java/java-servlet/) as a starting point.

## Style guide
The style tests for this repository are based on [Checkstyle](https://checkstyle.sourceforge.io/index.html), and are configured in [checkstyle.xml](checkstyle.xml) and [checkstyle-suppressions.xml](checkstyle-suppressions.xml). If you wish to modify the style checks, you may want to check out some of Checkstyle's documentation, in particular [the list of available checks](https://checkstyle.sourceforge.io/checks.html). The tests are run with the [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/), whose configuration is defined in [pom.xml](pom.xml).

It's worth noting that "TODO" comments are reported as a style violation (albeit as a warning, not as an error). This is done not to discourage the use of these comments (feel free to use them when they are appropriate), but to make sure that they are not forgotten.

The style tests can be run with the command `mvn checkstyle:check`.
