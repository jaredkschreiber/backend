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

### Required Environment Variables
Environment Variable | Default | Description
-------------------- | ------- | -----------
`BOOKSELF_DB_HOST` | `localhost` | The database host
`BOOKSELF_DB_NAME` | `bookself` | The database name
`BOOKSELF_DB_PORT` | `5432` | The database port
`BOOKSELF_DB_USER` | `postgres` | The database user name
`BOOKSELF_DB_PASS` |  | The database password
`BOOKSELF_CORS_ALLOWED_ORIGINS` | `*` | CORS allowed origins (Should be the front-end url unless we want to allow all origins)
`BOOKSELF_POPULARITY_CRON_SCHEDULE` | `0 0 0 * * *` i.e. Everyday at midnight | Schedule for popularity computation

### Running the Backend Application
`$ ./gradlew :apis:bootRun`

__OR, while in the same directory as the executable JAR,__

`$ java -jar apis-0.0.1-SNAPSHOT.jar`

### Endpoints

Method | Endpoint | Description | Truly Restful
------ | -------- | ----------- | -------------
POST | `/v1/auth/signin` | Sign in | ✅
GET | `/v1/auth/signout` | Sign out | ✅
GET | `/v1/books/{id}` | Get a book by id | ✅
GET | `/v1/books/any` | Get random books (the number of books returned is configurable) | ❌ 🔜 `/v1/books`
GET | `/v1/books/by-author?authorId=1234` | Get book whose author has an id of `1234` | ❌ 🔜 `/v1/books?authorId=1234`
GET | `/v1/books/by-genre?genre=Some+Genre` | Get a book by genre (Works with a single genre only) | ❌ 🔜 `/v1/books?genre=History`
GET | `/v1/genres/any` | Get random genres (the number of genres returned is configurable) | ❌ 🔜 `/v1/genres`
GET | `/v1/authors/{id}` | Get author by id | ✅
GET | `/v1/authors/any` | Get random authors (the number of authors returned is configurable) | ❌ 🔜 `/v1/authors`
GET | `/v1/book-lists/{id}` | Get book list by id | ✅
GET | `/v1/book-lists/{id}/books` | Get the books (ids only) in a book list | ❌ 🔜 
PUT | `/v1/book-lists/{id}/update` | Update book list: Rename, add books, remove books | ❌ 🔜 
GET | `/v1/users/{id}/book-lists` | Get book lists of a user | ❌ 🔜 
POST | `/v1/books/{bookId}/rating` | Add a new rating to the book (authenticated request) | ✅
PATCH | `/v1/books/{bookId}/rating` | Update rating (authenticated request) | ❌ 🔜 `/v1/books/{bookId}/rating/{ratingId}`
DELETE | `/v1/books/{bookId}/rating` | Update rating (authenticated request) | ❌ 🔜 `/v1/books/{bookId}/rating/{ratingId}`
GET | `/v1/users/{id}` | Get user by id | ✅
POST | `/v1/users/new-user` | Add a new user | ❌ 🔜 `/v1/users`
GET | `/v1/books/populars` | Get all popular books | ❓
GET | `/v1/books/populars?genre=Some+Genre` | Get all popular books in a given genre | ❓
