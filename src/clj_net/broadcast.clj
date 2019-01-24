(ns clj-net.broadcast
  (require [clj-net.core :refer :all]
           [clojure.pprint :as pp]))

(defn p1
  [addrs]
  (loop [msgs #{}]
    (pp/pprint msgs)
    (recur (conj msgs (orecv)))))

(defn bracha-broadcast
  ([addrs obj]
    (obroadcast addrs obj)
    (bracha-broadcast addrs))
  ([addrs]))

(defn -main
  []
  (let [addrs [{:host "localhost" :port 3333}
               {:host "localhost" :port 3333}
               {:host "localhost" :port 3333}
               {:host "localhost" :port 3333}]]
    (obroadcast addrs {:type :initial :v 0})
    (obroadcast addrs {:type :initial :v 1})
    (obroadcast addrs {:type :echo :v 0})
    (obroadcast addrs {:type :initial :v 1})
    (p1 addrs)))
