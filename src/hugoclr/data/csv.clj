;;---------------------------------------------------------------------------
;; csv.clj
;; The code in this file is used to parse the HUGO award pages and create a
;; a record for each nonimee/winner
;;---------------------------------------------------------------------------
(ns hugoclr.data.csv)

(defn- clean 
  "Replaces commas with semi-colons, removes HTML tags, and converts vals to
   their character representation using the .NET String.Replace method"
  [val]
    (.Replace (.Replace (.Replace (.Replace (.Replace val "," ";") "<em>" "") "</em>" "") "&#8217;" "'") "&amp;" "&"))

(defn- delimit
  "Converts the works Record into a comma-delimited string."
  [year books]
  (map #(str year "," (:winner %) "," 
                      (clean (:title %)) "," 
                      (:author %) "," 
                      (clean (:publisher %)) "\r\n") books))

(defn write-to-file
  "writes the comma-delimited strings out to the path passed in in file-name."
  [categories file-name]
    (let [stream (System.IO.StreamWriter. file-name)
          lines (map #(delimit (:year (first %)) (:books (first %))) categories)]
      (.Write stream (apply str (map #(reduce str "" %) lines)))
      (.Close stream)))
  
