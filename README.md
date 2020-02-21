# pat-or-nat-backend


## Database setup
You need to have docker and docker-compose installed. 

Navigate to the folder "database" and execute the script `initialize_database.sh` and the database will build, setup and run. 

The database will by default be available at port localhost:27017 and mongo-express GUI is available in dev mode on localhost:8081.


### Connection
You'll need to set some environment variables in order to connect to the database. In Linux, this can be done by adding the following lines to `/etc/environment`, making sure to replace the password with your own password:

```console
export PATORNAT_MONGODB_HOST=localhost
export PATORNAT_MONGODB_PORT=27017
export PATORNAT_MONGODB_USERNAME=user
export PATORNAT_MONGODB_PASSWORD=secret
export PATORNAT_MONGODB_DATABASE=patornat
```
And for the docker-compose container you need these environment variables

```console
export PATORNAT_MONGODB_ROOT_USERNAME=root
export PATORNAT_MONGODB_ROOT_PASSWORD=secret
export PATORNAT_MONGODB_INITDB_DATABASE=patornat
```
Then run the command `source /etc/environment`. If this does not successfully set the environment variables, you may have to reboot the system. Note that Tomcat will have to be restarted in order for changes in environment variables to take effect in the application.

Connection to the database is established when the server is run through MongoDBContextListener.  
The database client is ready to use for all servlets via the context variable MONGO_CLIENT.

`MongoClient client = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");`

All connections to the DB will go through Database Access Objects (DAO) while the models provide the internal logic needed for non database actions.


## Endpoints

### Post - /scoring
```json 
{
  "chosen": {
    "id": 1,
    "comment": "testcomment",
    "kjernestruktur": 1,
    "cellegrenser": 1,
    "kontrastKollagen": 1,
    "kontrastBindevev": 1
  },
  "other": {
    "id": 2,
    "comment": "testcomment2",
    "kjernestruktur": 2,
    "cellegrenser": 3,
    "kontrastKollagen": 2,
    "kontrastBindevev": 3
  }
}

```

## Testing
Before you can run the integration tests for the first time, you have to set some environment variables:

```console
export PATORNAT_TEST_MONGODB_HOST=localhost
export PATORNAT_TEST_MONGODB_PORT=27018
export PATORNAT_TEST_TOMCAT_HOST=localhost
export PATORNAT_TEST_TOMCAT_PORT=8081
export PATORNAT_TEST_MONGODB_USERNAME=testuser
export PATORNAT_TEST_MONGODB_PASSWORD=testpassword
export PATORNAT_TEST_MONGODB_DATABASE=patornat_test
# The following variable should normally be set to http,
# but if for some reason you've configured your test instance
# of the application to use HTTPS, then it should be https
export PATORNAT_TEST_TOMCAT_PROTOCOL=http
# The following three variables are only needed if you're
# starting the database with docker-compose
export PATORNAT_TEST_MONGODB_ROOT_USERNAME=admin
export PATORNAT_TEST_MONGODB_ROOT_PASSWORD=hunter2
export PATORNAT_TEST_MONGODB_INITDB_DATABASE=patornat_test

```

The integration tests will only work if the application is already running. If you have docker-compose installed,
you can start it in a test container with the command `sudo -E ./integration_test_setup.sh`,
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
