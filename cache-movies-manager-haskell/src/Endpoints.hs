{-# LANGUAGE DataKinds       #-} -- for [Json] and the String like the url
{-# LANGUAGE TemplateHaskell #-} -- for the deriveJson method
{-# LANGUAGE TypeOperators   #-} -- for the :>
{-# LANGUAGE FlexibleContexts #-} -- for the 404
{-# LANGUAGE OverloadedStrings #-} -- for the error message in the 404

module Endpoints
    ( startEndpoints
    ) where

-- affine et clean tes imports?
import Data.Aeson
import Network.Wai
import Network.Wai.Handler.Warp
import Control.Monad.IO.Class (liftIO)
import Servant

import MoviesImporter
import Model
import RedisConnector

type MoviesAPI = "cache" :> "refresh" :> Get '[PlainText] String --should be Put?
               :<|> "movie" :> Capture "movieId" Int :> Get '[JSON] Movie
               :<|> "movies" :> QueryParams "id" Int :> Get '[JSON] [Movie]

movieProxy :: Proxy MoviesAPI
movieProxy = Proxy

movieServer :: Server MoviesAPI
movieServer = importServer
           :<|> singleMovieServer
           :<|> multipleMoviesServer

  where  importServer = do
                          movies <- liftIO $ importMovies
                          liftIO $ saveMovies movies
                          return "Import in progress\n"

         singleMovieServer movieId = do
                          movie <- liftIO $ getMovie movieId
                          if movie == emptyMovie -- I didn't want to use exceptions to drive the code, return Maybe or Either from connector instead
                            then throwError $ err404 { errBody = "No movie found with this id in the cache" }
                            else return movie

         multipleMoviesServer movieIds = do
                          movies <- liftIO $ getMovies movieIds
                          return movies


app :: Application
app = serve movieProxy movieServer

startEndpoints :: IO ()
startEndpoints = run 8080 app


-- for metrics : https://github.com/fimad/prometheus-haskell/tree/master/example

--                 .route(GET(URL_PREFIX + "/movie/{movieId}"), handler::getMovie)
   --                .andRoute(GET(URL_PREFIX + "/movies"), handler::getMovies)
   --                .andRoute(GET(URL_PREFIX + "/cache/refresh"), handler::refreshCache)
   --                .andRoute(GET("/readiness"), handler::isCacheReady);
