# Compare Backend

## About
TODO

## Requirements
To build the application, you need Docker and Docker Compose. For development and testing, you also need Apache Maven 3 and JDK 1.8. You may also find it useful to have a local installation of Apache Tomcat 8 or higher.

## Setup
You'll need to set some environment variables. In Linux, you can do this by adding the following lines to `/etc/environment`, making sure to replace things like passwords and keys:

```bash
export PATORNAT_MONGODB_PORT=27017
export PATORNAT_MONGODB_USERNAME=user
export PATORNAT_MONGODB_PASSWORD=secret
export PATORNAT_MONGODB_DATABASE=patornat
export PATORNAT_MONGODB_ROOT_USERNAME=root
export PATORNAT_MONGODB_ROOT_PASSWORD=secret
export PATORNAT_MONGODB_INITDB_DATABASE=patornat

export PATORNAT_CYTOMINE_URL=http://core.example.com
export PATORNAT_CYTOMINE_PROJECT_ID=123456
export PATORNAT_CYTOMINE_PUBLIC_KEY=01234567-89ab-cdef-0123-456789abcdef
export PATORNAT_CYTOMINE_PRIVATE_KEY=01234567-89ab-cdef-0123-456789abcdef

```

Then run the command `source /etc/environment`. This should set the environment variables in your current shell.
In order for the changes to take effect globally, you may have to reboot your system.

The variables starting with `PATORNAT_CYTOMINE_` should contain the URL of an existing Cytomine instance, a project containing
the images that are to be compared by end users, and the public and private keys of a Cytomine user who has read access to the project.

## Testing
Before you can run the integration tests for the first time, you have to set some environment variables:

```bash
export PATORNAT_TEST_TOMCAT_HOST=localhost
export PATORNAT_TEST_TOMCAT_PORT=8081
export PATORNAT_TEST_TOMCAT_PROTOCOL=http

export PATORNAT_TEST_MONGODB_HOST=localhost
export PATORNAT_TEST_MONGODB_PORT=27018
export PATORNAT_TEST_MONGODB_USERNAME=testuser
export PATORNAT_TEST_MONGODB_PASSWORD=testpassword
export PATORNAT_TEST_MONGODB_DATABASE=patornat_test
export PATORNAT_TEST_MONGODB_ROOT_USERNAME=admin
export PATORNAT_TEST_MONGODB_ROOT_PASSWORD=hunter2
export PATORNAT_TEST_MONGODB_INITDB_DATABASE=patornat_test

export PATORNAT_TEST_CYTOMINE_URL=http://core.example.com
export PATORNAT_TEST_CYTOMINE_PROJECT_ID=123456
export PATORNAT_TEST_CYTOMINE_PUBLIC_KEY=01234567-89ab-cdef-0123-456789abcdef
export PATORNAT_TEST_CYTOMINE_PRIVATE_KEY=01234567-89ab-cdef-0123-456789abcdef

```

The integration tests will only work if the application is already running. You can start it in a test container with the command `sudo -E ./integration_test_setup.sh`,
and stop it with the command `sudo -E ./integration_test_teardown.sh`.

To run the unit tests and integration tests:

```console
mvn verify

```

To run only the unit tests:

```console
mvn test

```

To run only the integration tests:

```console
mvn -Dskip.unit.tests=true verify

```
