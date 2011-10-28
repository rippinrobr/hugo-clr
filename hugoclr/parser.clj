(ns hugoclr.parser )

;; (System.Reflection.Assembly/LoadWithPartialName "System.Windows.Forms")
;; (import '(System.Windows.Forms MessageBox))

(defstruct work :winner :title :author)
(defstruct category :award :books :year)

(defn fetch-url [url]
  (println (str "Simulated call to " url)))