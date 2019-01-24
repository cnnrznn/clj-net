(ns clj-net.broadcast
  (require [clj-net.core :refer :all]))

(defn p1
  [socket addrs]
  (loop [msgs []]
    (pprint msgs)
    (if (>= (count msgs) 4)
      msgs
      (recur (conj msgs (orecv socket))))))

(defn p2
  [socket addrs msgs]
  (recur socket addrs (into msgs (orecv socket))))

(defn p3
  [socket addrs msgs]
  (recur socket addrs (into msgs (orecv socket))))

(defn bracha_recv_broadcast
  [socket addrs msg]
  (let [m1 (p1 socket addrs)
        m2 (p2 socket addrs m1)
        result (p3 socket addrs m2)]
    result))
