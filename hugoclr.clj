;; I ran gacutil -i HtmlAgilityPack in the libs dir as administrator
;; to load the dll into the gac for simplicity.
(System.Reflection.Assembly/LoadWithPartialName "HtmlAgilityPack")

(ns hugoclr
  (:gen-class)
  (:use hugoclr.parser))

(def base-url "http://www.thehugoawards.org/hugo-history/")

(defn print-href-value [anchor]
    (println (.Value (first (.Attributes anchor)))))

(defn -main [& args]
   (println (str "base-url: " base-url))
   (let [nodes (hugoclr.parser/get-award-links hugoclr/base-url)]
     (println (apply str (map print-href-value nodes)))))
     ;; (println (.Value (first (.Attributes (first nodes)))))))
