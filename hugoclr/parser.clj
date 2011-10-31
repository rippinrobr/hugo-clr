;; I ran gacutil -i HtmlAgilityPack in the libs dir as administrator
;; to load the dll into the gac for simplicity.
(System.Reflection.Assembly/LoadWithPartialName "HtmlAgilityPack")

(ns hugoclr.parser )
  
(defstruct work :winner :title :author)
(defstruct category :award :books :year)

(defn fetch-url [url]
  "fetches the web page and converts it into a .NET object"
  (.Load (new HtmlAgilityPack.HtmlWeb) url))
        
(defn get-links [url]
  "Gets all <a> that match the xpath and returns a collection of .NET objects"
  (.SelectNodes (.DocumentNode (hugoclr.parser/fetch-url url)) "//li[@class]/a[@href]"))

(defn validate-award-link 
  "Filters out all non-award links so that I only retrieve pages that list nominees and 
   winners"
  [url] (re-matches #".*hugo-history.*/.+" (.Value (first (.Attributes url)))))

(defn get-awards [url]
  (let [links (get-links url)]
    (apply str (println (filter #(not (nil? %)) (map validate-award-link links))))))