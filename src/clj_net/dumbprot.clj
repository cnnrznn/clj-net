(ns clj-net.dumbprot
  (:require [clojure.pprint :refer [pprint]]
            [clj-net [util :as u]
                     [core :as c]]))

(defn state-change
  "Dumb state change. Pick value of majority"
  [v messages]
  (u/majority-or messages v))

(defn dumbprot
  [addrs v]
  (c/obroadcast addrs v)
  (let [messages (u/recv2f1 addrs)]
    (recur addrs (state-change v messages))))

(defn -main
  [value]
  (dumbprot [{:host "100.10.10.10" :port 3333}
             {:host "100.10.10.11" :port 3333}
             {:host "100.10.10.12" :port 3333}
             {:host "100.10.10.13" :port 3333}]
            (u/parse-int value)))
