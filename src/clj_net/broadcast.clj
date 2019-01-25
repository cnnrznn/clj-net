(ns clj-net.broadcast
  (require [clj-net [core :refer :all]
                    [util :as util]]
           [clojure.pprint :as pp]))

(defn phase1
  [addrs]
  (loop [msgs #{}]
    (pp/pprint msgs)
    (recur (conj msgs (util/validate msgs (orecv))))))

(defn bracha-broadcast
  ([addrs obj]
    (obroadcast addrs obj)
    (bracha-broadcast addrs))
  ([addrs]
    (let [m1 (phase1 addrs)]
      m1)))

(defn -main
  ([si v]
    (let [addrs [{:host "" :port 3333}
                 {:host "" :port 3333}
                 {:host "" :port 3333}
                 {:host "" :port 3333}]
          i (util/parse-int si)]
    (if (= 0 i)
      (bracha-broadcast addrs {:type :initial
                               :v v
                               :id i
                               :r 0})
      (bracha-broadcast addrs))
    )))
