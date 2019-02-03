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

(defn mfilter
  [msgs k v]
  (filter (fn [x] (= (k x) v))
          msgs))

(defn initial
  [msgs]
  (mfilter msgs :type "initial"))

(defn echo
  [msgs]
  (mfilter msgs :type "echo"))

(defn ready
  [msgs]
  (mfilter msgs :type "ready"))

(defn validate
  [msgs new]
  new)

(defn phase1
  [addrs]
  (loop [msgs #{}]
    (let [proceed (or (>= (count (initial msgs)) 1)
                      (>= (count (echo msgs)) (n-f (count addrs)))
                      (>= (count (ready msgs)) (f-1 (count addrs))))]
      (if proceed
        msgs
        (recur (conj msgs (validate msgs (orecv))))))))

(defn phase2
  [addrs msgs]
  (let [proceed (or (>= (count (echo msgs)) (n-f (count addrs)))
                    (>= (count (ready msgs)) (f-1 (count addrs))))]
    (if proceed
      msgs
      (recur addrs (conj msgs (validate msgs (orecv)))))))

(defn phase3
  [addrs msgs]
  (let [proceed (>= (count (ready msgs)) (n-f (count addrs)))]
    (if proceed
      msgs
      (recur addrs (conj msgs (validate msgs (orecv)))))))

(defn accept
  [msgs]
  (let [msg (first (ready msgs))]
    (:v msg)))

(defn bracha-broadcast
  ([i addrs obj]
    (obroadcast addrs obj)
    (bracha-broadcast i addrs))
  ([i addrs]
    (let [m1 (phase1 addrs)
          _ (pp/pprint m1)
          _ (obroadcast addrs {:type "echo"
                               :v (:v (first m1))
                               :id i
                               :r 0})
          m2 (phase2 addrs m1)
          _ (pp/pprint m2)
          _ (obroadcast addrs {:type "ready"
                               :v (:v (first m2))
                               :id i
                               :r 0})
          m3 (phase3 addrs m2)
          _ (pp/pprint m3)]
      (accept m3))))

(defn -main
  ([si v]
    (let [addrs [{:host "100.10.10.10" :port 3333}
                 {:host "100.10.10.11" :port 3333}
                 {:host "100.10.10.12" :port 3333}
                 {:host "100.10.10.13" :port 3333}]
          i (util/parse-int si)]
      (pp/pprint (format "I am process %d" i))
      (if (= 0 i)
        (bracha-broadcast i addrs {:type "initial"
                                   :v v
                                   :id i
                                   :r 0})
        (bracha-broadcast i addrs)))))
