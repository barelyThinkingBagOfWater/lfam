{-# LANGUAGE DataKinds       #-} -- for [Json] and the String like the url
{-# LANGUAGE TypeOperators   #-} -- for the :>

module MoviesImporter (importMovies) where

import Data.Aeson
import Data.Proxy
import Network.HTTP.Client (newManager, defaultManagerSettings)
import Servant.Client
import Servant
import Model

type MoviesManagerAPI = "movies-manager" :> "movies" :> Get '[JSON] [Movie]

moviesManagerAPI :: Proxy MoviesManagerAPI
moviesManagerAPI = Proxy

importMoviesClient :: ClientM [Movie]
importMoviesClient = client moviesManagerAPI


importMovies :: IO [Movie] --Try using a maybe so you can return Nothing in case of error
importMovies = do
  manager' <- newManager defaultManagerSettings
  answer <- runClientM importMoviesClient (mkClientEnv manager' (BaseUrl Http "172.18.42.5" 80 ""))
  case answer of
    Left err -> do
      putStrLn $ "Error when importing the movies: " ++ show err
      return []
    Right (movies) -> do -- better handling including errors please
      return movies
