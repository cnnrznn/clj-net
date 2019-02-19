(ns clj-net.pbft
  (require [clojure.pprint :refer [pprint]]
           [clj-net [core :refer :all]]))

(defn pre-prepare
  [pid addrs view seqn request]
  (let [message {:type :pre-prepare
                 :request request
                 :view view
                 :seq seqn
                 :sender pid}

(defn pbft
  [pid addrs]
  (let [view 0
        seqn 0
        N (count addrs)
        leader? (mod view N)]
    (if leader?
                                ; for now, randomly generate event
      ()
                                ; wait for leader with timeout
      ())))
