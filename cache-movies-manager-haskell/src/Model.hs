{-# LANGUAGE TemplateHaskell #-} -- for the deriveJson method

module Model (Movie, getMovieId, emptyMovie) where

import Data.Aeson.TH


data Movie = Movie
  { movieId :: Int
  , title :: String
  , genres :: String
  , tags :: [String]
  } deriving (Show, Eq) -- for printing

$(deriveJSON defaultOptions 'Movie)

getMovieId :: Movie -> Int -- should be created by default? Why 'movieId movie' doesn't work elsewhere?
getMovieId movie = movieId movie

emptyMovie :: Movie
emptyMovie = Movie 0 "noTitle" "noGenre" [] --kind of null object pattern

-- Get accessor for each field, or use different constructor, or?
--addTag :: String -> Movie
--addTag tag = do
