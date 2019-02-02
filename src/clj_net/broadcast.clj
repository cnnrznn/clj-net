(ns clj-net.broadcast
  (require [clj-net [core :refer :all]
                    [util :as util]]
           [clojure.pprint :as pp]))

(defn n-f
  [n]
  (let [f (quot (- n 1) 3)]
    (- n f)))

(defn f-1
  [n]
  (let [f (quot (- n 1) 3)]
    (+ f 1)))

(defn proceed?
  [n m]
  (let [f (/ (- n 1) 3)
        thresh (- n f)]
    (>= m thresh)))

(defn phase1
  [addrs]
  (loop [msgs #{}]
    (pp/pprint msgs)
    (let [proceed (and (>= (count (filter)) 1))]
                        ;(>= (count (filter)) (n-f (count addrs)))
                        ;(>= (count (filter)) (f-1 (count addrs))))]
    (if proceed
      msgs
      (recur (conj msgs (util/validate msgs (orecv))))))))

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
      (pp/pprint (format "I am process %d" i))
      (if (= 0 i)
        (bracha-broadcast addrs {:type :initial
                                 :v v
                                 :id i
                                 :r 0})
        (bracha-broadcast addrs)))))
