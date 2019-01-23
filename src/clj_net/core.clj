(ns clj-net.core
  (require [clojure.pprint :refer [pprint]]))

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

(defn broadcast
  [socket addrs msg]
  (doall (map (fn [{:keys [host port]}]
                (send socket host port msg))
              addrs)))

(defn recv
  [socket]
  (let [buffer (byte-array 1024)
        packet (DatagramPacket. buffer 1024)]
    (.receive socket packet)
    (String. (.getData packet) 0 (.getLength packet))))

(defn -main
  []
  (broadcast sk [{:host "localhost" :port 3333}] "Hello, self!")
  (pprint (recv sk)))
