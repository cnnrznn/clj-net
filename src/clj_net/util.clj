(ns clj-net.util
  (:require [clj-net [core :as c]]))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn validate
  [messages new]
  new)

(defn mfilter
  [msgs k v]
  (filter (fn [x] (= (k x) v))
          msgs))

(defn vec-remove
  "remove elem in coll"
  [coll pos]
   (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn recv2f1
  [addrs]
  (let [n (count addrs)
        f (quot (- n 1) 3)
        ff (* 2 f)]
    (loop [msgs []]
      (if (> (count msgs) ff)
        (msgs)
        (recur (conj msgs (c/orecv)))))))
