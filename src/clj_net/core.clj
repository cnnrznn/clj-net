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
  [addrs obj]
  (broadcast sk addrs (json/write-str obj)))
