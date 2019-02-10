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
  [round msgs new]
  (let [test_r (not= round (:round new))
        match_s (mfilter msgs :sender (:sender new))
        match_o (mfilter match_s :owner (:owner new))
        matches match_o]
    (if (or test_r
            (>= (count matches) 1))
      nil
      new)))

(defn echo
  [addrs id new]
  (when (= (:owner new) (:sender new))
    (obroadcast addrs (assoc new :sender id))))

(defn terminate?
  [n msgs]
  (let [freq (frequencies (map :value msgs))
        elem (first (filter (fn [x] (>= (nth x 1) (n-f n)))
                              freq))
        result (nth elem 0 nil)]
    (if (and (= nil result)
             (>= (count msgs) (n-f n)))
      "Dishonest leader"
      result)))

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
      (if (terminate? (count addrs) msgs)
        (terminate? (count addrs) msgs)
        (let [msg (validate r msgs (orecv))]
          (when msg
            (echo addrs i msg))
          (recur (conj msgs msg)))))))

(defn -main
  [id_str r_str]
  (let [id (util/parse-int id_str)
        r (util/parse-int r_str)
        addrs [{:host "100.10.10.10" :port 3333}
               {:host "100.10.10.11" :port 3333}
               {:host "100.10.10.12" :port 3333}
               {:host "100.10.10.13" :port 3333}]]
    (pp/pprint
      (if (= 0 id)
        (zcast addrs id r "foo")
        (zcast addrs id r)))))
