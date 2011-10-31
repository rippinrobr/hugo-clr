;; I ran gacutil -i HtmlAgilityPack in the libs dir as administrator
;; to load the dll into the gac for simplicity.
(System.Reflection.Assembly/LoadWithPartialName "HtmlAgilityPack")

(ns hugoclr
  (:gen-class)
  (:use hugoclr.parser))

(def base-url "http://www.thehugoawards.org/hugo-history/")

(defn print-href-value [anchor]
    (.Value (first (.Attributes anchor))))

(defn -main [& args]
   (println (str "base-url: " base-url))
   (hugoclr.parser/get-awards base-url))  
