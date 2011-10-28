(ns hugoclr
  (:gen-class)
  (:use hugoclr.parser))

(def base-url "http://www.thehugoawards.org/hugo-history/")


(defn -main [& args]
   (println (str "base-url: " base-url))
   (hugo.parser/fetch-url base-url))
