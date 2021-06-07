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
`BOOKSELF_EMAIL_HOST` | `smtp.gmail.com` | The email host
`BOOKSELF_EMAIL_PORT` | `587` | The email port
`BOOKSELF_EMAIL_USER` | `bookselfservice@gmail.com` | The email username
`BOOKSELF_EMAIL_PASS` | `password` | The email password
`BOOKSELF_APP_URL` | `https://bookself.xyz` | The app URL

### Running the Backend Application
`$ ./gradlew :apis:bootRun`

__OR, while in the same directory as the executable JAR,__

`$ java -jar apis-0.0.1-SNAPSHOT.jar`

### Endpoints

Method | Endpoint | Description | Truly Restful
------ | -------- | ----------- | -------------
GET | `/v1/books` | Get any set of books | ✅
GET | `/v1/books?authorId=12345` | Get books by author | ✅
GET | `/v1/books?genre=Some+Genre` | Get books by genre | ✅
POST | `/v1/auth/signin` | Sign in | ✅
POST | `/v1/auth/signout` | Sign out | ✅
POST | `/v1/auth/forgot-password` | Forgot password | ❓
POST | `/v1/auth/reset-password` | Reset password | ❓
GET | `/v1/books/{id}` | Get a book by id | ✅
GET | `/v1/genres` | Get random genres (the number of genres returned is configurable) | ✅
GET | `/v1/genres?popular=yes` | Get popular genres (currently only `?popular=yes` works) | ✅
GET | `/v1/authors/{id}` | Get author by id | ✅
GET | `/v1/authors` | Get random authors (the number of authors returned is configurable) | ✅
GET | `/v1/book-lists/{id}` | Get book list by id | ✅
GET | `/v1/book-lists/{id}/books` | Get the books (ids only) in a book list | ❌ 🔜
PUT | `/v1/book-lists/{id}/update` | Update book list: Rename, add books, remove books | ❌ 🔜
GET | `/v1/users/{id}/book-lists` | Get book lists of a user | ❌ 🔜
POST | `/v1/books/{bookId}/rating` | Add a new rating to the book (authenticated request) | ✅
PATCH | `/v1/books/{bookId}/rating` | Update rating (authenticated request) | ❌ 🔜 `/v1/books/{bookId}/rating/{ratingId}`
DELETE | `/v1/books/{bookId}/rating` | Update rating (authenticated request) | ❌ 🔜 `/v1/books/{bookId}/rating/{ratingId}`
GET | `/v1/users/{id}` | Get user by id | ✅
POST | `/v1/users/new-user` | Add a new user | ❌ 🔜 `/v1/users`
DELETE | `/v1/users/{id}` | Delete a user | ✅
GET | `/v1/books/populars` | Get all popular books | ❓
GET | `/v1/books/populars?genre=Some+Genre` | Get all popular books in a given genre | ❓
