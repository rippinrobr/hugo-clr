;;---------------------------------------------------------------------------
;; hugoclr.clj
;; The main function lives here.  It simply calls the function to kick off
;; the retrieval/parsing and writes the results to file system
;;---------------------------------------------------------------------------
(assembly-load-from "..\\libs\\HtmlAgilityPack.dll")

(ns hugoclr
  (:gen-class) ;; generates the class and makes our exe.  Without it
               ;; only a dll is created.
  (:use hugoclr.parser)
  (:use hugoclr.data.csv))

;; the hugo site's page that lists all of the years they have data for
(def base-url "http://www.thehugoawards.org/hugo-history/")

(defn -main [& args]
   "runs the parser and writes the results to the hugo.txt file"
   (println (str "base-url: " base-url))
   (let [awards (hugoclr.parser/get-awards base-url)]
     (hugoclr.data.csv/write-to-file awards "c:\\temp\\hugo.txt")))

