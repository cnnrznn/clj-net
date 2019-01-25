(ns clj-net.util)

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defn validate
  [messages new]
  new)
