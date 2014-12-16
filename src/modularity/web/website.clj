(ns modularity.web.website
  (:require
   [bidi.bidi :refer (path-for)]
   [bidi.ring :refer (redirect)]
   [clojure.pprint :refer (pprint)]
   [clojure.edn :as edn]
   [clojure.tools.logging :refer :all]
   [clojure.java.io :as io]
   [com.stuartsierra.component :refer (using)]
   [hiccup.core :as hiccup]
   [modular.bidi :refer (WebService as-request-handler)]
   [modular.ring :refer (WebRequestHandler)]
   [modular.template :refer (render-template template-model)]
   [modularity.web.markdown :refer (markdown)]
   [ring.util.response :refer (response)]
   [tangrammer.component.co-dependency :refer (co-using)]
   [leiningen.new.modular :refer (load-manifest)]))

(defn menu [router uri]
  (hiccup/html
   [:ul.nav.masthead-nav
    (for [[k label] [[::index "Home"]
                     [::templates "Templates"]
                     [:modularity.web.book/book "Book"]
                     [::about "About"]
                     ]

          :let [href (path-for (:routes @router) k)]]

      [:li (when (= href uri) {:class "active"})
       [:a (merge {:href href}) label]]
      )]))

(defn page [templater router req content]
  (response
   (render-template
    templater
    "templates/page.html.mustache" ; our Mustache template
    {:menu (menu router (:uri req))
     :content content})))

(defn white [templater router req content menu]
  (response
   (render-template
    templater
    "templates/book.html.mustache" ; our Mustache template
    {:content content
     :chapters menu})))

(defn index [templater router]
  (fn [req]
    (page templater router req
          (hiccup/html
           [:div
            [:h1.cover-heading "Modularity"]
            [:p.lead "Create a Clojure-powered website like this one. We'll call it " [:tt "foo"] "."]
            [:code.lead "lein new modular foo bootstrap-cover"]

            ]))))

#_(edn/read (java.io.PushbackReader. (io/reader (io/input-stream "/home/malcolm/src/modular/lein-template/resources/manifest.edn"))))

#_(load-manifest "/home/malcolm/src/modular/lein-template/resources/manifest.edn" "foo")

(defn templates [templater router manifest]
  (fn [req]
    (white templater router req
           (hiccup/html
           [:div
            [:h1.cover-heading "Available templates"]
            [:div
             (markdown (io/resource "markdown/templates.md"))]
            (for [tm (keys (:application-templates (load-manifest manifest "foo")))]
              [:p [:a {:href (path-for (:routes @router) ::template :template tm)} tm]])])
           nil)))

(defn template-page [templater router manifest]
  (fn [{rp :route-params :as req}]
    (let [template (:template rp)
          man (load-manifest manifest "foo")]
      (white templater router req
             (hiccup/html
             [:div
              [:h1.cover-heading template]
              (when-let [res (io/resource (format "markdown/%s.md" template))]
                [:div
                 (markdown res)])
              (let [tm (get (:application-templates man) template)
                    modules (filter (comp (:modules tm) :module) (:modules man))]
                [:div
                 [:h2 "Incantation"]
                 [:p "To create a project called " [:tt "foo"] " based on this template, type this in a command line shell :-"]
                 [:code.lead (format "lein new modular foo %s" template)]

                 [:h2 "Modules"]
                 (for [m modules]
                   [:p (:module m)])

                 [:h2 "Re-used modular components"]
                 (for [m (distinct (sort (keep :component (mapcat vals (map :components modules)))))
                       ]
                   [:p [:a {:href (format "https://clojars.org/juxt.%s" (clojure.string/replace (str (namespace m)) "." "/"))} (str (namespace m)) ]  "/" (name m)]
                   )

                 [:h2 "Additional libraries"]
                 (for [[name version] (distinct (sort (mapcat :library-dependencies modules)))]
                   [:p [:a {:href (format "https://clojars.org/%s/versions/%s" name version)}
                        (hiccup/h [name version])]])

                 [:h2 "Files"]
                 (for [filename (distinct (sort (map :target (mapcat :files modules))))]
                   [:p filename])


                 #_[:pre {:style "text-align: left"} (with-out-str (clojure.pprint/pprint modules))]
                 ])

              ])
             (for [chapter (keys (:application-templates (load-manifest manifest "foo")))]

               {:title chapter
                :link (path-for (:routes @router) ::template :template chapter)}
               )))))

(defn about [templater router]
  (fn [req]
    (page templater router req
          (hiccup/html
           [:div
            [:h1.cover-heading "About"]
            [:div
             (markdown (io/resource "markdown/about.md"))]
            ]))))

;; Components are defined using defrecord.

(defrecord Website [manifest templater router]

  ; modular.bidi provides a router which dispatches to routes provided
  ; by components that satisfy its WebService protocol
  WebService
  (request-handlers [this]
    ;; Return a map between some keywords and their associated Ring
    ;; handlers
    {::index (index templater router)
     ::templates (templates templater router manifest)
     ::template (template-page templater router manifest)
     ::about (about templater router)})

  ;; Return a bidi route structure, mapping routes to keywords defined
  ;; above. This additional level of indirection means we can generate
  ;; hyperlinks from known keywords.
  (routes [_] ["/" {"index.html" ::index
                    "templates.html" ::templates
                    ["templates/" :template ".html"] ::template
                    "" (redirect ::index)
                    "about.html" ::about}])

  ;; A WebService can be 'mounted' underneath a common uri context
  (uri-context [_] ""))

;; While not mandatory, it is common to use a function to construct an
;; instance of the component. This affords the opportunity to control
;; the construction with parameters, provide defaults and declare
;; dependency relationships with other components.

(defn new-website [& {:as args}]
  (-> (->> args
           (map->Website))
      (using [:templater])
      (co-using [:router])))
