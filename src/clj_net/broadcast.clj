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

(defn majority-value
  [msgs]
  (let [target (->> msgs
                    (map :v)
                    (frequencies)
                    (vec)
                    (sort-by second)
                    (reverse)
                    (first)
                    (first))
        _ (pp/pprint valfreq)]
    (filter (fn [x] (= target (:v x))) msgs)))

(defn initial
  [msgs initiator]
  (-> msgs
      (mfilter :type "initial")
      (mfilter :initiator initiator)))

(defn echo
  [msgs]
  (-> msgs
      (mfilter :type "echo")
      (majority-value)))

(defn ready
  [msgs]
  (-> msgs
      (mfilter :type "ready")
      (majority-value)))

(defn accept
  [msgs]
  (:v (first (ready msgs))))

(defn validate-func
  [initiator round]
  (fn
    [msgs new]
    (let [matches (-> msgs
                     (mfilter :sender (:sender new))
                     (mfilter :type (:type new)))
          valid (and (= initiator (:initiator new))
                     (= round (:round new)))]
      (if (or (not valid)
              (>= (count matches) 1))
        nil
        new))))

(defn phase1-func
  [validate initiator]
  (fn
    [addrs]
    (loop [msgs #{}]
      (let [proceed (or (>= (count (initial msgs initiator)) 1)
                        (>= (count (echo msgs)) (n-f (count addrs)))
                        (>= (count (ready msgs)) (f-1 (count addrs))))]
        (if proceed
          msgs
          (recur (conj msgs (validate msgs (orecv)))))))))

(defn phase2-func
  [validate]
  (fn
    [addrs msgs]
    (let [proceed (or (>= (count (echo msgs)) (n-f (count addrs)))
                      (>= (count (ready msgs)) (f-1 (count addrs))))]
      (if proceed
        msgs
        (recur addrs (conj msgs (validate msgs (orecv))))))))

(defn phase3-func
  [validate]
  (fn
    [addrs msgs]
    (let [proceed (>= (count (ready msgs)) (n-f (count addrs)))]
      (if proceed
        msgs
        (recur addrs (conj msgs (validate msgs (orecv))))))))

(defn broadcast-func
  [initiator round]
  (let [validate (validate-func initiator round)
        phase1 (phase1-func validate initiator)
        phase2 (phase2-func validate)
        phase3 (phase3-func validate)]
    (fn
      [pid addrs]
        (let [m1 (phase1 addrs)
              _ (pp/pprint m1)
              _ (obroadcast addrs {:type "echo"
                                   :v (:v (first m1))
                                   :sender pid
                                   :initiator initiator
                                   :round round})
              m2 (phase2 addrs m1)
              _ (pp/pprint m2)
              _ (obroadcast addrs {:type "ready"
                                   :v (:v (first m2))
                                   :sender pid
                                   :initiator initiator
                                   :round round})
              m3 (phase3 addrs m2)
              _ (pp/pprint m3)]
          (accept m3)))))

(defn -main
  ([si v]
    (let [addrs [{:host "100.10.10.10" :port 3333}
                 {:host "100.10.10.11" :port 3333}
                 {:host "100.10.10.12" :port 3333}
                 {:host "100.10.10.13" :port 3333}]
          i (util/parse-int si)]
      (pp/pprint (format "I am process %d" i))
      (when (= 0 i)
        (obroadcast addrs {:type "initial"
                           :v v
                           :sender i
                           :initiator 0
                           :round 0}))
      (let [broadcast (broadcast-func 0 0)]
        (broadcast i addrs)))))
