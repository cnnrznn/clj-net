(ns clj-net.pbft
  (require [clojure.pprint :refer [pprint]]
           [clj-net [core :refer :all
                    [util :as u]]]))

(defn accept-pp?
  [view log msg]
  (and (= view (:view msg))
       (>= (count (-> log
                      (u/mfilter :view (:view msg))
                      (u/mfilter :seq (:seq msg))
                      (u/mfilter :type :pre-prepare)))
           1)))

(defn pre-prepare
  ([pid addrs view seqn log]            ; listen for pre-prepare
   (let [message (orecv)
         accept? (accept-pp? view log)]
     (when accept?
       (conj log message))
     (prepare pid addrs view seqn log)))
  ([pid addrs view seqn log request]    ; broadcast pre-prepare
   (let [message {:type :pre-prepare
                  :request request
                  :view view
                  :seq seqn
                  :sender pid}
         log (conj log message)
         dst (u/vec-remove addrs pid)]
     (obroadcast dst message)
     (prepare pid addrs view seqn (conj log message)))))

(defn prepare
  [pid addrs view seqn log]
  (pprint "Done!"))

(defn pbft
  [pid addrs]
  (let [view 0
        seqn 0
        N (count addrs)
        leader? (mod view N)
        log []]
    (if leader?
                                ; for now, randomly generate event
      (pre-prepare pid addrs view seqn log "the request")
                                ; wait for leader with timeout
      (pre-prepare pid addrs view seqn log))))

(defn -main
  [si]
  (let [addrs [{:host "100.10.10.10" :port 3333}
               {:host "100.10.10.11" :port 3333}
               {:host "100.10.10.12" :port 3333}
               {:host "100.10.10.13" :port 3333}]
        i (u/parse-int si)]
    (pprint (format "I am process %d" i))
    (pbft pid addrs)))
