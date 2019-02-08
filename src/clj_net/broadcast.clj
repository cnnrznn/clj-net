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

(defn validate
  [msgs new]
  (let [match_r (mfilter msgs :round (:round new))
        match_s (mfilter match_r :sender (:sender new))
        match_o (mfilter match_s :owner (:owner new))
        matches match_o]
    (if (>= (count matches) 1)
      nil
      new)))

(defn zcast
  ([addrs i r v]
    (obroadcast addrs {:owner i
                       :sender i
                       :round r
                       :value v})
    (zcast addrs i r))
  ([addrs i r]
    (loop [msgs #{}]
      (pp/pprint msgs)
      (pp/pprint)
      (recur (conj msgs (validate msgs (orecv)))))))

(defn -main
  [id_str r_str]
  (let [id (util/parse-int id_str)
        r (util/parse-int r_str)
        addrs [{:host "100.10.10.10" :port 3333}
               {:host "100.10.10.11" :port 3333}
               {:host "100.10.10.12" :port 3333}
               {:host "100.10.10.13" :port 3333}]]
    (if (= 0 id)
      (zcast addrs id r "foo")
      (zcast addrs id r))))
