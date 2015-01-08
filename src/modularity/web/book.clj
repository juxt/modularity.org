(ns modularity.web.book
  (:require
   [bidi.ring :refer (redirect)]
   [clojure.edn :as edn]
   [clojure.java.io :refer (resource) :as io]
   [com.stuartsierra.component :refer (using)]
   [hiccup.core :as hiccup]
   [modular.bidi :refer (WebService)]
   [modular.template :refer (render-template)]
   [modularity.web.markdown :refer (markdown)]
   [ring.util.response :refer (response)]
   [schema.core :as s]
   [tangrammer.component.co-dependency :refer (co-using)]))

(defn get-chapters [book-dir]
  (for [line (line-seq (io/reader (io/file (io/as-file book-dir) "Book.txt")))
        :let [f (io/file book-dir line)]
        :when (and (.isFile f) (.exists f))
        :let [chapter (second (re-matches #"(.*).md" line))
              raw (first (line-seq (io/reader f)))
              title (second (re-matches #"# (.*)" raw))]]
    {:title title
     :link (str chapter ".html")}))

(defn page [templater router req content]
  (response
   (render-template
    templater
    "templates/book.html.mustache" ; our Mustache template
    {:content content})))

(defn book [templater router book-dir]
  (fn [req]
    (page templater router req
          (hiccup/html
           [:div {:style "list-bullet-style: none"}
            [:h1.cover-heading "Book"]
            [:p (markdown (io/file book-dir "frontmatter.md"))]
            [:ul
             (for [{:keys [title link]} (get-chapters book-dir)]
               [:li [:a {:href link} title]])]]))))

(defn chapter [templater router book-dir]
  (fn [{{chapter :chapter} :route-params :as req}]
    (response
     (render-template
      templater
      "templates/book.html.mustache"    ; our Mustache template
      {:chapters (get-chapters book-dir)
       :content (markdown (io/file (io/as-file book-dir) (str chapter ".md")))}))))

(defrecord Website [templater router book-dir]

  ; modular.bidi provides a router which dispatches to routes provided
  ; by components that satisfy its WebService protocol
  WebService
  (request-handlers [this]
    ;; Return a map between some keywords and their associated Ring
    ;; handlers
    {::book (book templater router book-dir)
     ::chapter (chapter templater router book-dir)})

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
           (s/validate {:book-dir (s/protocol io/Coercions)})
           (map->Website))
      (using [:templater])
      (co-using [:router])))
