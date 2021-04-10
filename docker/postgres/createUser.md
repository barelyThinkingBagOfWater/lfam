docker exec bash

su - postgres

createuser movie-client;

psql
alter user "movie-client" with encrypted password 'movie-client123';
exit

createdb movies;
psql movies;
create schema movies;

psql movies (or without movies?)
grant all privileges on database movies to "movie-client";
grant all privileges on schema movies to "movie-client";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA movies TO "movie-client"; 
