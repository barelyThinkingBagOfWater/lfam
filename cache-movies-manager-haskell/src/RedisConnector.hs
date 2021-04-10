module RedisConnector (saveMovies, getMovie, getMovies) where

import Control.Monad.IO.Class (liftIO)
import Data.Aeson (encode, decode)

import qualified Data.ByteString.Char8      as  C (pack)
import           Data.ByteString.Lazy       (toStrict, fromStrict)

import Database.Redis (runRedis, connect, defaultConnectInfo, set, get, Status, Reply)
import Model

-- for config, read env variables, https://hackage.haskell.org/package/envy
saveMovie :: Movie -> IO (Either Reply Status)
saveMovie movie = do
  conn <- liftIO $ connect defaultConnectInfo -- you could try to add more max concurrent connections, check object ConnectInfo
  runRedis conn $ set (C.pack (show (getMovieId movie))) $ (toStrict $ encode movie) --show for Int -> String, C.pack for String -> C.ByteString


saveMovies :: [Movie] -> IO ()
saveMovies movies = do
  mapM_ saveMovie movies -- underscore to specify you don't care about the results


getMovie :: Int -> IO Movie
getMovie movieId = do
  conn <- liftIO $ connect defaultConnectInfo
  answer <- runRedis conn $ get (C.pack (show (movieId)))
  case answer of
    Left reply -> do
      print $ "There was a problem with Redis when fetching the movie: " ++ show reply
      return emptyMovie
    Right maybe -> do
      case maybe of
        Nothing -> do
          print $ "movie id:" ++ (show movieId) ++ " not found in cache"
          return emptyMovie
        Just encodedMovie -> do
          case (decode $ fromStrict encodedMovie :: Maybe Movie) of
            Nothing -> do
              print $ "Movie couldn't be decoded from Json stored in cache"
              return emptyMovie
            Just movie -> do
              return movie

getMovies :: [Int] -> IO [Movie]
getMovies movieIds = do
  mapM getMovie movieIds


--addTagToMovie :: String -> String -> IO ()
--addTagToMovie tag movieId = do
--  movie <- getMovie movieId


-- TODO: to add a tag you'll need to use a temp state, it's gonna take some time

-- connection lost est pas du à la limite des 100k avec redis? If you consume a stream one by one
-- that should go better with Redis as well

-- https://hackage.haskell.org/package/hedis-0.12.11/docs/Database-Redis.html
-- Connection to the server lost:
   --  In case of a lost connection, command functions throw a ConnectionLostException.
   --  It can only be caught outside of runRedis.
   --  Hardcore : https://www.schoolofhaskell.com/user/snoyberg/general-haskell/exceptions/catching-all-exceptions

