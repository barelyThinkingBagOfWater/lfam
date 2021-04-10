{-# LANGUAGE TypeOperators   #-} -- for the :>

module Main where

import Endpoints
import TagsListener

main :: IO ()
--main = startEndpoints
main = testAmqp
