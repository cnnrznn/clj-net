(ns clj-net.broadcast
  (require [clj-net [core :refer :all]
                    [util :as util]]
           [clojure.pprint :as pp]))

(defn proceed?
  [n m]
  (let [f (/ (- n 1) 3)
        thresh (- n f)]
    (>= m thresh)))

(defn phase1
  [addrs]
  (loop [msgs #{}]
    (if (proceed? (count addrs) (count msgs))
      msgs
      (recur (conj msgs (util/validate msgs (orecv)))))))

(defn bracha-broadcast
  ([addrs obj]
    (obroadcast addrs obj)
    (bracha-broadcast addrs))
  ([addrs]
    (let [m1 (phase1 addrs)]
      m1)))

(defn -main
  ([si v]
    (let [addrs [{:host "100.10.10.10" :port 3333}
                 {:host "100.10.10.11" :port 3333}
                 {:host "100.10.10.12" :port 3333}
                 {:host "100.10.10.13" :port 3333}]
          i (util/parse-int si)]
    (if (= 0 i)
      (bracha-broadcast addrs {:type :initial
                               :v v
                               :id i
                               :r 0})
      (bracha-broadcast addrs))
    )))
