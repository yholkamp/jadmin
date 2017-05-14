# JAdmin

JAdmin is a [Spark Java](http://sparkjava.com/) powered framework to create simple back-ends for administration tasks. Use this library as part 
of your existing JVM application or stand alone to easily expose CRUD access to rows in your SQL tables, `.properties` 
files and any other data. In the current set-up, JAdmin is focused on providing trusted users with access to data, 
it is not yet suitable to provide access to untrusted users.

Inspired by the Ruby [Active Admin](https://github.com/activeadmin/activeadmin) library.

# Usage

Add the library as Maven dependency to your Java 8 (and up) application and call the fluent API to add resources you'd 
like to expose through the admin panel:

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

Customizing the templates used by JAdmin is possible as well, all templates that may be overwritten are in the 
`src/main/resources/jadmin` folder. To overwrite or customize a template, create an 
`src/main/resources/jadmin` folder in your own application and create a copy of the file you would like to customize. 
JAdmin will now load the copy of the file provided by your application. For details on the available template syntax,
take a look at [Freemarker](http://freemarker.org/docs/index.html), the template engine used by JAdmin.

To overwrite any translation strings, table or column names and more, copy the `en.properties` to the `resources/jadmin/i18n` 
directory in your own project and edit the file to your liking. 

# Roadmap

* Improve the test coverage
* Include smart support for foreign keys, i.e. display a `<select>` for foreign key columns
* Include CSRF tokens in each form
* Support file-based configuration
* Perform an overall security review of the project.
* Allow front-end input validation rules to be configured.
* Add a (before/after) hook system to customize rendered and saved values.

# Limitations

For performance reasons, JAdmin will currently not actively reload the table definition for a default SQL resource. 
This means that if you update your table definition, you'll have to restart the application for these to show up.

# License

MIT License

Copyright (c) 2016 Yorick Holkamp

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.