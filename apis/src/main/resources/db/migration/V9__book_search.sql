CREATE TABLE search_index AS
    SELECT books.id, books.title, authors.name AS author, books.blurb, genres.genre
        FROM books
        INNER JOIN books_authors ON books.id = books_authors.book_id
        INNER JOIN authors ON authors.id = author_id
        INNER JOIN genres ON books.id = genres.book_id;


ALTER TABLE search_index
    ADD search tsvector
    GENERATED ALWAYS AS (
        (
            setweight(to_tsvector('simple',  title),  'A') || ' ' ||
            setweight(to_tsvector('simple',  author), 'B') || ' ' ||
            setweight(to_tsvector('english', blurb),  'C') || ' ' ||
            setweight(to_tsvector('simple',  genre),  'D')
        )::tsvector
    ) STORED;
