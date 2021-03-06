(ns clj-net.pbft
  (require [clojure.pprint :refer [pprint]]
           [clojure.core.async :as async]
           [clj-net [core :refer :all]
                    [util :as u]]))

(def msg-chan (async/chan 128))
(def tmt-chan (async/chan 128))

(defn leader
  [view n]
  (mod view n))

(defn accept-pp?
  [addrs view seqn log msg]
  (and (= view (:view msg))
       (= seqn (:seqn msg))
       (= (:sender msg) (leader view (count addrs)))
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
  [pid addrs view seqn log]
  (let [message (orecv)
        accept? (accept-pp? addrs view seqn log message)]
    (if accept?
      (let [prepare_msg {:type "prepare"
                         :view view
                         :seqn seqn
                         :sender pid}
            log (conj log message)]
        (obroadcast addrs prepare_msg)
        (prepare pid addrs view seqn log))
      (pprint "FATAL: received bad pre-prepare"))))

(defn pbft
  [pid addrs]
  (let [view 0
        seqn 0
        N (count addrs)
        leader? (= (mod view N) pid)
        log []]
    (when leader?
      (let [message {:type "pre-prepare"
                     :request request
                     :view view
                     :seqn seqn
                     :sender pid}]
        (obroadcast addrs message)))
    (pre-prepare pid addrs view seqn log)))

(defn -main
  [si]
  (let [addrs [{:host "100.10.10.10" :port 3333}
               {:host "100.10.10.11" :port 3333}
               {:host "100.10.10.12" :port 3333}
               {:host "100.10.10.13" :port 3333}]
        pid (u/parse-int si)]
    (pprint (format "I am process %d" pid))
    (async/<!! (async/go (pbft pid addrs)))))
