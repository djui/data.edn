(ns edn-data-gen.print.helpers
  "Helpers for printing collections, etc."
  (:require [edn-data-gen.print.protocols.writer :as writer]
            [edn-data-gen.print.protocols.printable :as printable]))

(defn write-character [c w]
  (writer/append w \\)
  (if-let [n (char-name-string c)]
    (writer/write w n)
    (writer/append w c)))

(defn write-string [s w]
  (writer/append w \")
  (dotimes [n (count s)]
    (let [c (.charAt s n)]
      (if-let [e (char-escape-string c)]
        (writer/write w e)
        (writer/append w c))))
  (writer/append w \"))

(defn print-seq-contents
  ([coll w opts separator]
     (print-seq-contents coll w opts (fn [o w opts]
                                       (printable/print o w opts)) separator))
  ([coll w opts print-one separator]
     (when (seq coll)
       (print-one (first coll) w opts))
     (doseq [x (next coll)]
       (writer/write w separator)
       (print-one x w opts))))

(defn print-sequential
  [coll w opts begin separator end]
  (writer/write w begin)
  (print-seq-contents coll w opts separator)
  (writer/write w end))

(defn print-map [m w opts]
  (writer/append w "{")
  (print-seq-contents (seq m) w opts
                  (fn [e w opts]
                    (do (printable/print (key e) w opts)
                        (writer/append w \space)
                        (printable/print (val e) w opts)))
                  ", ")
  (writer/write w "}"))