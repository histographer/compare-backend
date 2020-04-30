# Compare Backend
This is the backend/middleware component of the Compare application. TODO link to more info

## Getting started
### Prerequisites
In order to build the application, you need Docker and Docker Compose. For development and testing, you also need Apache Maven 3 and JDK 1.8.

### How to set environment variables
- [Windows](https://www.techjunkie.com/environment-variables-windows-10/)
- TODO MacOS
- Linux:
    - Open `/etc/environment` in a text editor
    - For each variable, add a line of the form `export VARIABLE_NAME=value`
    - Run `source /etc/environment` in a terminal
    - The variables should now be set for that terminal session.
    They will be set globally whenever the system reboots. 

### Installing
First, set the following environment variables:

```
MONGODB_INITDB_ROOT_USERNAME: username
MONGODB_INITDB_ROOT_PASSWORD: password
MONGODB_INITDB_DATABASE: compare
MONGODB_PORT: 27017
ANALYSIS_PROTOCOL: http
ANALYSIS_URL: analysis
COMPARE_CYTOMINE_URL: https://core.example.com
COMPARE_ADMIN_PUB_KEY: public key
COMPARE_ADMIN_PRIV_KEY: private key

```

The variable `COMPARE_CYTOMINE_URL` should contain the URL of the Cytomine instance hosting the images for comparison. The variables `COMPARE_ADMIN_{PUB, PRIV}_KEY` should contain a key pair that can be used to connect to this instance.

Secondly, clone [the analysis API repository](https://github.com/histographer/analysis-rest-api) into the directory `../analysis-rest-api` (relative to the directory containing this repository). (TODO: could we use the Docker image instead?) Copy the contents of `.analysis.env.sample` into a file called `.analysis_dev.env`.

Finally, run the command `docker-compose -f docker-compose.dev.yml up --build -d`. A development container will become available at `http://localhost:9292`.

## Testing
### Preparing to run the integration tests
Before you can run the integration tests, you have to set some environment variables:

```
COMPARE_TEST_MONGODB_HOST: localhost
COMPARE_TEST_MONGODB_PORT: 27018
COMPARE_TEST_MONGODB_ROOT_USERNAME: admin
COMPARE_TEST_MONGODB_ROOT_PASSWORD: password123
COMPARE_TEST_MONGODB_DATABASE: compare_test
COMPARE_TEST_TOMCAT_PROTOCOL: http
COMPARE_TEST_TOMCAT_HOST: localhost
COMPARE_TEST_TOMCAT_PORT: 8081

```

In addition, you need to copy the contents of the file `.analysis.env.sample` into a file with the name `.analysis_test.env`.

### Running the tests
The integration tests will only work if the application is already running. You can start it in a test container
with the command `./integration_test_setup.sh`, and stop it with the command `./integration_test_teardown.sh`.
You may need to add the prefix `sudo -E` to these commands.

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

To run the style tests:

```console
mvn checkstyle:check

```

## Deployment
TODO

## Documentation
The API is described in [API.md](API.md).

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md).

## License
TODO

## Acknowledgements
TODO
