(ns modularity.web.book
  (:require
   [bidi.ring :refer (redirect)]
   [clojure.edn :as edn]
   [clojure.java.io :refer (resource) :as io]
   [com.stuartsierra.component :refer (using)]
   [endophile.core :refer (mp to-clj)]
   [hiccup.core :as hiccup]
   [modular.bidi :refer (WebService)]
   [ring.util.response :refer (response)]
   [modular.template :refer (render-template)]
   [schema.core :as s]
   [tangrammer.component.co-dependency :refer (co-using)]))

(defn emit-element
  ;; An alternative emit-element that doesn't cause newlines to be
  ;; inserted around punctuation.
  [e]
  {:pre [e]}
  (if (instance? String e)
    (print e)
    (do
      (print (str "<" (name (:tag e))))
      (when (:attrs e)
	(doseq [attr (:attrs e)]
	  (print (str " " (name (key attr)) "='" (val attr)"'"))))
      (if (:content e)
	(do
	  (print ">")
          (if (instance? String (:content e))
            (print (:content e))
            (doseq [c (:content e)]
              (emit-element c)))
	  (print (str "</" (name (:tag e)) ">")))
	(print "/>")))))

(defn markdown [content]
  (->> content io/reader slurp mp to-clj (map emit-element) dorun with-out-str))

(defn page [templater router req content]
  (response
   (render-template
    templater
    "templates/book.html.mustache" ; our Mustache template
    {:content content})))

(defn book [templater router bookdir]
  (fn [req]
    (page templater router req
          (hiccup/html
           [:div {:style "list-style-type: none"}
            [:h1.cover-heading "Book"]
            [:ul
             (for [line (line-seq (io/reader (io/file (io/as-file bookdir) "Book.txt")))
                   :let [f (io/file bookdir line)]
                   :when (and (.isFile f) (.exists f))
                   :let [raw (first (line-seq (io/reader f)))
                         title (second (re-matches #"# (.*)" raw))]]
               [:li [:a {:href (str (second (re-matches #"(.*).md" line)) ".html")} title]])]]))))

(defn chapter [templater router bookdir]
  (fn [{{chapter :chapter} :route-params :as req}]
    (page templater router req
          (markdown (io/file (io/as-file bookdir) (str chapter ".md"))))))

(defrecord Website [templater router bookdir]

  ; modular.bidi provides a router which dispatches to routes provided
  ; by components that satisfy its WebService protocol
  WebService
  (request-handlers [this]
    ;; Return a map between some keywords and their associated Ring
    ;; handlers
    {::book (book templater router bookdir)
     ::chapter (chapter templater router bookdir)})

  ;; Return a bidi route structure, mapping routes to keywords defined
  ;; above. This additional level of indirection means we can generate
  ;; hyperlinks from known keywords.
  (routes [_] ["" {"/index.html" ::book
                    "/" (redirect ::book)
                    "" (redirect ::book)
                    ["/" :chapter ".html"] ::chapter}])

  ;; A WebService can be 'mounted' underneath a common uri context
  (uri-context [_] "/book"))

(defn new-book-website [& {:as opts}]
  (-> (->> opts
           (merge {})
           (s/validate {:bookdir (s/protocol io/Coercions)})
           (map->Website))
      (using [:templater])
      (co-using [:router])))
