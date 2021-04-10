{-# LANGUAGE OverloadedStrings #-} --for amqp I surmise, test without

module TagsListener (testAmqp) where

import Network.AMQP

import           Control.Monad (forM_)
import qualified Data.ByteString.Lazy.Char8 as BL
import           Data.Monoid ((<>))


tagsExchangeName = "tags.added.exchange"
tagsQueueName = "tags.added.queue.cache-movies-manager"

testAmqp :: IO ()
testAmqp = do
     conn <- openConnection "172.18.42.3" "/" "rabbit" "rabbit123"
     ch   <- openChannel conn

     declareExchange ch newExchange {exchangeName    = tagsExchangeName,
                                          exchangeType    = "fanout",
                                          exchangeDurable = True}
     (q, _, _) <- declareQueue ch newQueue {queueName       = tagsQueueName,
                                                 queueAutoDelete = False,
                                                 queueDurable    = True}
     bindQueue ch q tagsExchangeName tagsQueueName

     consumeMsgs ch q Ack deliveryHandler

     -- waits for keypresses
     getLine
     closeConnection conn

deliveryHandler :: (Message, Envelope) -> IO ()
deliveryHandler (msg, metadata) = do
  BL.putStrLn $ "Message received:" <> body
  ackEnv metadata
  where
    body = msgBody msg


