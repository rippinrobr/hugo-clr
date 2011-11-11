(ns hugoclr.data.csv)

(defn delimit
  [year books]
  (map #(str year "," (:winner %) "," (:title %) "," (:publisher %) "\r\n") books))

(defn write-to-file
  [categories file-name]
    (let [stream (System.IO.StreamWriter. file-name)
          lines (map #(delimit (:year (first %)) (:books (first %))) categories)]
      (.Write stream (apply str (map #(reduce str "" %) lines)))
      (.Close stream)))
  
