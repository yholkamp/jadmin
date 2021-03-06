# JAdmin

[![Build Status](https://travis-ci.org/yholkamp/jadmin.svg?branch=master)](https://travis-ci.org/yholkamp/jadmin)

JAdmin is a simple library to help provide a data administration UI to manage various data sources, both in your existing Java web application and stand alone. 
It can be used to easily expose CRUD access to data originating from a wide range of sources, including rows in your SQL tables, `.properties` files or data in NoSQL databases. 

In the current set-up, JAdmin is focused on providing trusted users with access to data, it is not yet suitable to provide access to untrusted users. 
Unlike [Light Admin](http://lightadmin.org/), JAdmin does not depend on Spring Boot or JPA, providing a lightweight and customizable solution.

Inspired by the Ruby [Active Admin](https://github.com/activeadmin/activeadmin) library.
 
# Usage

[![Javadocs](http://www.javadoc.io/badge/net.nextpulse/jadmin.svg)](http://www.javadoc.io/doc/net.nextpulse/jadmin) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.nextpulse/jadmin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.nextpulse/jadmin/)

Add the library to your Java 8+/Scala/Kotlin application:
     
For Maven users:

     <dependency>
         <groupId>net.nextpulse</groupId>
         <artifactId>jadmin</artifactId>
         <version>(version)</version>
     </dependency>

For sbt users:
     
     libraryDependencies += "net.nextpulse" % "jadmin" % "(version)"

For gradle users:
     
     repositories {
         mavenCentral()
     }
     
     dependencies {
         compile 'net.nextpulse:jadmin:(version)'
     }

And call the API to add resources you'd like to expose through the admin panel:

    JAdmin jAdmin = new JAdmin();
    
    // connect to a JDBC datasource and add the users table with default settings (all but the primary key columns are editable, all columns are included on the table index)
    DataSource source = retrieveMyDataSource();
    jAdmin.resource("users", source);

Alternatively, you can tailor the exposed columns to your own needs as follows:

    jAdmin.resource("users", source)
        .formConfig((form) -> form
            // add a header, grouping an username and password input field
            .inputGroup("Login credentials", (group) -> group.input("username").input("password"))
            // add a header with an is_admin input field
            .inputGroup("Permissions", (group) -> group.input("is_admin"))
            // add some text
            .paragraph("Press cancel to go back.")
            // include the submit & cancel buttons
            .actions()
        )
        // custom index
        .indexConfig((index) -> index
            // do show the user id, while not exposing this through the edit page
            .column("id")
            // show the username
            .column("username")
            // do show the created_at column on the index page
            .column("created_at")
        );

If you would like to expose a resource that is not backed by a (JDBC compliant) SQL database, it's also possible to 
provide your own implementation of the `AbstractDAO` and `ResourceSchemaProvider` classes. As an example of a DAO 
implementation, take a look at the `InMemoryDAO` implementation.

# Configuration and customization

Customizing the templates and translation files used by JAdmin is done by placing customized versions of the files in the 
`src/main/resources/jadmin/templates` and `src/main/resources/jadmin/i18n` folder of your own project. 
When a template (or translation file) is loaded, JAdmin will first look in these directories before falling back on 
the `/net/nextpulse/jadmin` directory.

For details on the available template syntax, take a look at [Freemarker](http://freemarker.org/docs/index.html), the template engine used by JAdmin.

# Roadmap

* Improve the test coverage
* Include smart support for foreign keys, i.e. display a `<select>` for foreign key columns
* Include CSRF tokens in each form
* Support file-based configuration
* Perform an overall security review of the project.
* Allow front-end input validation rules to be configured.

# Limitations

For performance reasons, JAdmin will currently not actively reload the table definition for a default SQL resource. 
This means that if you update your table definition, you'll have to restart the application for these to show up.
