;; I ran gacutil -i HtmlAgilityPack in the libs dir as administrator
;; to load the dll into the gac for simplicity.
(System.Reflection.Assembly/LoadWithPartialName "HtmlAgilityPack")

(ns hugoclr.parser )

(defstruct work :winner :title :author)
(defstruct category :award :books :year)

(defn fetch-url [url]
  (println (str "retrieving " url "..."))
  (.Load (new HtmlAgilityPack.HtmlWeb) url))
        
(defn get-award-links [url]
  (.SelectNodes (.DocumentNode (hugoclr.parser/fetch-url url)) "//li[@class]/a[@href]"))
     ;; "//li[@class]/a[@href]"))
