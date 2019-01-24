(ns clj-net.core
  (require [clojure.pprint :refer [pprint]]
           [clojure.data.json :as json]))

(import '[java.net DatagramSocket
                   DatagramPacket
                   InetSocketAddress])

(def sk (DatagramSocket. 3333))

(defn send
  [socket host port msg]
  (let [payload (.getBytes msg)
        length (min (alength payload) 1024)
        address (InetSocketAddress. host port)
        packet (DatagramPacket. payload length address)]
    (.send socket packet)))

(defn recv
  [socket]
  (let [buffer (byte-array 2048)
        packet (DatagramPacket. buffer 2048)]
    (.receive socket packet)
    (String. (.getData packet) 0 (.getLength packet))))

(defn broadcast
  [socket addrs msg]
  (doall (map (fn [{:keys [host port]}]
                (send socket host port msg))
              addrs)))

(defn osend
  [socket host port obj]
  (send socket host port (json/write-str obj)))

(defn orecv
  [socket]
  (json/read-str (recv socket)
                 :key-fn keyword))

(defn obroadcast
  [socket addrs obj]
  (broadcast socket addrs (json/write-str obj)))

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

(defn -main
  []
  (let [addrs [{:host "localhost" :port 3333}
                 {:host "localhost" :port 3333}
                 {:host "localhost" :port 3333}
                 {:host "localhost" :port 3333}]]
    (broadcast sk addrs {:type :initial :v 0})
    (pprint (p1 sk addrs))))
