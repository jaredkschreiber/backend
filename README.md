# Book Self
## A Books Recommendation Web Application

#### SE491 - Software Engineering Studio
**DePaul University, College of Computing and Digital Media**

*Spring 2021*

## Team Members
1. David Engel
2. Christian Kleinvehn
3. Kyle Olson
4. Jared Schreiber
5. Lisa Sun
6. Nardos Tessema

## Development
### Requirements
JDK 11 is required to build and run this application.

Since we're using the Gradle Wrapper, there is no need to install Gradle locally. Just use `./gradlew` or `gradle.bat`.

### Running the Backend Application
`$ ./gradlew :apis:bootRun`

__OR, while in the same directory as the executable JAR,__

`$ java -jar apis-0.0.1-SNAPSHOT.jar`

### Endpoints

Endpoint | Description
-------- | -----------
`/ping` | Health Check endpoint
`/v1/books/{id}` | Get a book by id
`/v1/books/all` | Get random books (the limit is configurable)
`/v1/books/by-author?authorId=1234` | Get book by author
`/v1/books/by-genre?genre=Some+Genre` | Get a book by genre (Works with a single genre only)
`/v1/genres/all` | Get random genres (the limit is configurable)
`/v1/authors/{id}` | Get author by id

