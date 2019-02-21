(ns clj-net.pbft
  (require [clojure.pprint :refer [pprint]]
           [clojure.core.async :as async]
           [clj-net [core :refer :all]
                    [util :as u]]))

(def msg-chan (async/chan 128))
(def tmt-chan (async/chan 128))

(defn accept-pp?
  [view seqn log msg]
  (and (= view (:view msg))
       (= seqn (:seqn msg))
       (< (count (-> log
                     (u/mfilter :view (:view msg))
                     (u/mfilter :seqn (:seqn msg))
                     (u/mfilter :type "pre-prepare")))
           1)))

(defn prepare
  [pid addrs view seqn log]
  (pprint "Done!")
  log)

(defn pre-prepare
  ([pid addrs view seqn log]            ; listen for pre-prepare
   (let [message (orecv)
         accept? (accept-pp? view seqn log message)]
     (if accept?
       (prepare pid addrs view seqn (conj log message))
       (pprint "FATAL: received bad pre-prepare"))))
  ([pid addrs view seqn log request]    ; broadcast pre-prepare
   (let [message {:type "pre-prepare"
                  :request request
                  :view view
                  :seqn seqn
                  :sender pid}
         dst (u/vec-remove addrs pid)]
     (obroadcast dst message)
     (prepare pid addrs view seqn (conj log message)))))

(defn pbft
  [pid addrs]
  (let [view 0
        seqn 0
        N (count addrs)
        leader? (= (mod view N) pid)
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
        pid (u/parse-int si)]
    (pprint (format "I am process %d" pid))
    (async/<!! (async/go (pbft pid addrs)))))
