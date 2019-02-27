(ns clj-net.dumbprot
  (:require [clojure.pprint :refer [pprint]]
            [clj-net [util :as u]
                     [core :as c]]))

(defn state-change
  [v messages]
  (pprint messages)
  v)

(defn dumbprot
  [addrs v]
  (c/obroadcast addrs v)
  (let [messages (u/recv2f1 addrs)]
    (recur addrs (state-change v messages))))

(defn -main
  [value]
  (dumbprot [{:host "127.0.0.1" :port 3333}
             {:host "127.0.0.1" :port 1201}
             {:host "127.0.0.1" :port 1202}
             {:host "127.0.0.1" :port 1203}]
            value))
