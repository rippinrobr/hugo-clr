;; I ran gacutil -i HtmlAgilityPack in the libs dir as administrator
;; to load the dll into the gac for simplicity.
(System.Reflection.Assembly/LoadWithPartialName "HtmlAgilityPack")

(ns hugoclr
  (:gen-class)
  (:use hugoclr.parser)
  (:use hugoclr.data.csv))

(def base-url "http://www.thehugoawards.org/hugo-history/")

(defn -main [& args]
   (println (str "base-url: " base-url))
   (let [awards (hugoclr.parser/get-awards base-url)]
     (hugoclr.data.csv/write-to-file awards "c:\\temp\\hugo.txt")))

