(ns clj-net.util)

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
