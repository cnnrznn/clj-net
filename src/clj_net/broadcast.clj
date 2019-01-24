(ns clj-net.broadcast
  (require [clj-net.core :refer :all]))

(defn p1
  [addrs]
  (loop [msgs []]
      msgs
      (recur (conj msgs (orecv))))))

(defn p2
  [addrs msgs]
  (recur addrs (into msgs (orecv))))

(defn p3
  [addrs msgs]
  (recur addrs (into msgs (orecv))))

(defn bracha_recv_broadcast
  [addrs]
  (let [m1 (p1 addrs)
        m2 (p2 addrs m1)
        result (p3 addrs m2)]
    result))

(defn -main
  []
  (let [addrs [{:host "localhost" :port 3333}
               {:host "localhost" :port 3333}
               {:host "localhost" :port 3333}
               {:host "localhost" :port 3333}]]
    (obroadcast addrs {:type :initial :v 0})
    (bracha_recv_broadcast addrs)))
