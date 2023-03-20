# Kuehne + Nagel: Homework (backend)

RESTful backend.

## Building

### Requirements
JDK17

### Build command

`gradlew clean build`

### Run as deploy pack
`java -jar kn-homework.jar`

### Dockerization
Not worth it in the current state. Can be included in the future

## Using

### Accessing the Backend

After running the application (with the default configuration) it will repond on: `http://localhost:8666/kn/api/{endpoint}`

The documentation is provided via Swagger and can be accessed at: `http://localhost:8666/kn/swagger-ui.html`

### Using the Backend

First step is to login. Post a JSON (`Content-Type: application/json`) body containing the username and credentials (`{"username":"{username}", "credentials":"{password}"}`) against the login endpoint (`http://localhost:8666/kn/api/login`).
 
**Note:** Currently only JSON body login is supported. Can be extended with "form-data" if required

The response will contain a "token" attribute. For subsequent request use the token as Bearer token: `Authorization: Bearer {token}`. 

With the bearer token set, the cities endpoints are now accessible: `GET http://localhost:8666/kn/api/cities`. More info in Swagger documentation

### Users and permissions
For the demo, the users are included in the application

 - "anon:" (empty password) - "ANON" role -  No permissions
 - "user:password" - "USER" role - 'View' permissions
 - "admin:admin" - "ADMIN" role - 'View' and 'Edit' permissions