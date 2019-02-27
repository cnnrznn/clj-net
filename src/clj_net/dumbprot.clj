(ns clj_net.dumbprot
  (:require [clojure.pprint :refer [pprint]]
            [clj_net.util :as u]))

(defn state-change
  [v messages]
  v)

(defn dumbprot
  [addrs v]
  (obroadcast addrs v)
  (let [messages (u/recv2f addrs)]
    (recur addrs (state-change v messages))))

(defn -main
  [value]
  (dumbprot [{:host  :port}
             {:host  :port}
             {:host  :port}
             {:host  :port}]
            value))
