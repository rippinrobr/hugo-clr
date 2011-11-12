;; I ran gacutil -i HtmlAgilityPack in the libs dir as administrator
;; to load the dll into the gac for simplicity.
(System.Reflection.Assembly/LoadWithPartialName "HtmlAgilityPack")

(ns hugoclr.parser )
  
(defstruct work :winner :title :author :publisher)
(defstruct category :award :books :year)

(defn fetch-url [url]
  "fetches the web page and converts it into a .NET object"
  (println (str "fetching " url ))
  (.Load (new HtmlAgilityPack.HtmlWeb) url))

(defn my-regex 
  [pattern target-str]
  (last (re-matches pattern target-str)))

(defn get-html-elements [url xpath]
  "Gets all <a> that match the xpath and returns a collection of .NET objects"
  (let [nodes (.SelectNodes (.DocumentNode (hugoclr.parser/fetch-url url)) xpath)]
        nodes))

(defn validate-award-link 
  "Filters out all non-award links so that I only retrieve pages that list nominees and 
   winners."
  [url] (re-matches #".*hugo-history.*/.+" (.Value (first (.Attributes url)))))

(defn get-year
  [p-node] (apply str (take 4 (.InnerHtml (second (.ChildNodes (.ParentNode p-node)))))))

(defn get-work-title
  [li-node]  (.InnerHtml (first (.ChildNodes li-node))))

(defn get-work-author-and-publisher
  [li-node] 
  (if (nil? (.ChildNodes li-node)) 
    (.InnerHtml li-node) 
    (.InnerHtml (second (.ChildNodes li-node)))))

(defn get-category-heading 
    [p-node] (.InnerHtml (first (.SelectNodes p-node "./strong"))))

(defn check-for-winner
  [li-node] 
    (if (and (not (nil? (.Attributes li-node))) (> (.Count (.Attributes li-node)) 0)) 
        (= "winner" (.Value (first (.Attributes li-node)))) 
        false))

(defn create-work-struct
  [li-node]
   (println (.InnerHtml li-node))
   (struct work (check-for-winner li-node) (get-work-title li-node) 
                (my-regex #".*</em>\s*(by|,)\s+(.*)\s+[\[\(].*" (.InnerHtml li-node))
                (my-regex #".*[\(\[](.*)[\)\]].*" (.InnerHtml li-node))))
    
(defn create-works-seq 
  [lis] (map create-work-struct (seq lis)))
  
(defn create-category-struct
  [ul] 
    (let [p-node (.PreviousSibling (.PreviousSibling ul))
          lis (filter #(= "li" (.OriginalName %)) (rest (.ChildNodes ul)))]
          (struct category (get-category-heading p-node) (create-works-seq (seq lis)) (get-year p-node))))

(defn parse-awards-page 
  "Gets all the book related sections of the web page. The first 5 items are book related."
  [award-url] 
    (let [top-node (get-html-elements award-url "//div[@id='content']/ul")]
       (map create-category-struct top-node)))
   
   
(defn get-awards 
  "gets all the links from the http://www.thehugoawards.org/hugo-history/ that lead to a awards page"
  [url]
  (let [links (get-html-elements url "//li[@class]/a[@href]")
        award-links (filter #(not (nil? %)) (map validate-award-link links))]
        (map parse-awards-page (take 12 award-links))))