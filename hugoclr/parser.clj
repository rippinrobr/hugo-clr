(ns hugoclr.parser )

(defstruct work :winner :title :author)
(defstruct category :award :books :year)

(defn fetch-url [url]
  (println (str "retrieving " url "..."))
  (let [res (.GetResponse (System.Net.WebRequest/Create url))
        sr (new System.IO.StreamReader (.GetResponseStream res))        ]
        (.ReadToEnd sr)))
 