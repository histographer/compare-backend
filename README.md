# pat-or-nat-backend


## Database setup
You need to have docker and docker-compose installed. 

Navigate to the folder "database" and execute the script `initialize_database.sh` and the database will build, setup and run. 

The database will by default be available at port localhost:27017 and mongo-express GUI is available in dev mode on localhost:8081.


### Connection
Set all necessary variables in `web.xml`

Connection to the database is established when the server is run through MongoDBContextListener.  
The database client is ready to use for all servlets via the context variable MONGO_CLIENT.

`MongoClient client = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");`

All connections to the DB will go through Database Access Objects (DAO) while the models provide the internal logic needed for non database actions.



