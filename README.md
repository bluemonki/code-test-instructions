# UrlShortenR

## Requirements
* Java 17

## API

A Java spring boot based application

### Local Running
Run the Unit tests for the API:
`./mvnw test`

Build and package the API using Maven:
`./mvnw package`

This will create a JAR file to run like so:
`java -jar target/urlshortener-0.0.1-SNAPSHOT.jar`

### Docker 

From the root directory:
* Build the app via maven
  * `./mvnw package`
* Build the dockerfile
  * `docker build --tag urlshortenr --file Dockerfile .`
* Run the dockerfile
  * `docker container run -d --name web1 -p 8080:8080 urlshortenr:latest`
* Run the dockerfile with a `/data` volume to persist the database
  * `docker container run -d --name web1 -p 8080:8080 -v /host/path:/data urlshortenr:latest`

## UI

A react-router based web application

### Local Running
Run the unit tests for the UI:
`npm test`

From the `ui/urlshortener-app` directory run:
`npm run dev` this will run a local setup on `http://localhost:5173/` and expect to connect to an API running on `localhost:8080`

### Docker
* Build the dockerfile
  * `docker build --tag urlshortenrui --file Dockerfile .`

* Run the dockerfile
  * `docker container run -d --name ui -p 80:3000 urlshortenrui:latest`


## ToDo

* Add some jest tests for the UI
* Use a database and not a hashmap
* Captcha