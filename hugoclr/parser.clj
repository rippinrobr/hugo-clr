;;---------------------------------------------------------------------------
;; parser.clj
;; The code in this file is used to parse the HUGO award pages and create a
;; a record for each nonimee/winner
;; 
;; I ran gacutil -i HtmlAgilityPack in the libs dir as administrator
;; to load the dll into the gac for simplicity.
;;---------------------------------------------------------------------------
(System.Reflection.Assembly/LoadWithPartialName "HtmlAgilityPack")

(ns hugoclr.parser )
 
;; stores the information about each nominee 
(defrecord Work [winner title author publisher])

;; stores the nominees and winner(s) for a category
;; in this case the Best Novel category
(defrecord Category [award books year])

(defn fetch-url [url]
  "fetches the web page and converts it into a .NET object using the
   HtmlAgilitPack assembly"
  (println (str "fetching " url ))
  (.Load (new HtmlAgilityPack.HtmlWeb) url))

(defn get-html-elements [url xpath]
  "Gets all <a> that match the xpath and returns a collection of .NET objects that 
  represents the <a> nodes"
  (let [nodes (.SelectNodes (.DocumentNode (hugoclr.parser/fetch-url url)) xpath)]
        nodes))

(defn validate-award-link 
  "Filters out all non-award links so that I only retrieve pages that list nominees and 
   winners."
  [url] (re-matches #".*hugo-history.*/.+" (.Value (first (.Attributes url)))))

(defn get-year
  "Gets the year for the category being parsed. It retrieves the year from the <h2> tag.
   It traverses the DOM heirarchy to get to the h2 tag and grab the text.  Then it takes the 
   first 4 chars which represents the year."
  [p-node] (apply str (take 4 (.InnerHtml (second (.ChildNodes (.ParentNode p-node)))))))

(defn get-work-title
  "parses the books/works title from the em tags."
  [li-node]  (.InnerHtml (first (.ChildNodes li-node))))

(defn get-category-heading
  "parses the category's title.  We could use this code to grab all of the categories. I'm
   only interested in the novels." 
  [p-node] (.InnerHtml (first (.SelectNodes p-node "./strong"))))

(defn check-for-winner
  "checks to see if the class attribute of the li tag is set to winner.  If so the work in 
   question was the winner in the HUGO category."
  [li-node] 
    (if (and (not (nil? (.Attributes li-node))) (> (.Count (.Attributes li-node)) 0)) 
        (= "winner" (.Value (first (.Attributes li-node)))) 
        false))

(defn create-work-record
  "Simply creates a record that represents a work "
  [li-node]
   (Work. (check-for-winner li-node) (get-work-title li-node) 
            (last (re-matches #".*</em>\s*(by|,)\s+(.*)\s+[\[\(].*" (.InnerHtml li-node)))
            (last (re-matches #".*[\(\[](.*)[\)\]].*" (.InnerHtml li-node)))))
    
(defn create-works-seq 
  "Creates all of the works in a given category represented by the sequence of li tags passed
  in"
  [lis] (map create-work-record (seq lis)))
  
(defn create-category-record
  "Creates a record that represents the category represented by the passed in ul tag."
  [ul] 
    (let [p-node (.PreviousSibling (.PreviousSibling ul))
          lis (filter #(= "li" (.OriginalName %)) (rest (.ChildNodes ul)))]
          (Category. (get-category-heading p-node) (create-works-seq (seq lis)) (get-year p-node))))
 
(defn parse-awards-page 
  "Gets all the ul tags in the book related sections of the web page. After the page has been parsed
  the category records are created."
  [award-url] 
    (let [top-node (get-html-elements award-url "//div[@id='content']/ul")]
       (map create-category-record top-node)))
    
(defn get-awards 
  "gets all the links from the http://www.thehugoawards.org/hugo-history/ that lead to a awards page"
  [url]
  (let [links (get-html-elements url "//li[@class]/a[@href]")
        award-links (filter #(not (nil? %)) (map validate-award-link links))]
        (map parse-awards-page (take 12 award-links))))