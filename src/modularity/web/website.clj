(ns modularity.web.website
  (:require
   [bidi.bidi :refer (path-for)]
   [bidi.ring :refer (redirect)]
   [clojure.pprint :refer (pprint)]
   [clojure.tools.logging :refer :all]
   [com.stuartsierra.component :refer (using)]
   [hiccup.core :as hiccup]
   [modular.bidi :refer (WebService as-request-handler)]
   [modular.ring :refer (WebRequestHandler)]
   [modular.template :refer (render-template template-model)]
   [ring.util.response :refer (response)]
   [tangrammer.component.co-dependency :refer (co-using)]))

(defn menu [router uri]
  (hiccup/html
   [:ul.nav.masthead-nav
    (for [[k label] [[::index "Home"]
                     [::features "Features"]
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

(defn index [templater router]
  (fn [req]
    (page templater router req
          (hiccup/html
           [:div
            [:h1.cover-heading "Modularity"]
            [:p.lead "Create a Clojure-powered website like this one. We'll call it " [:tt "foo"] "."]
            [:code.lead "lein new modular foo bootstrap-cover"]

            ]))))

(defn features [templater router]
  (fn [req]
    (page templater router req
          (hiccup/html
           [:div
            [:h1.cover-heading "Features"]
            [:p.lead "bootstrap-cover exhibits the following :-"]
            [:ul.lead
             [:li "A working Clojure-powered website using Stuart Sierra's 'reloaded' workflow and component library"]
             [:li "A fully-commented route-contributing website component"]
             [:li [:a {:href "https://github.com/juxt/bidi"} "Bidi"] " routing"]
             [:li "Co-dependencies"]
             [:li "Deployable with lein run"]
             ]
            ]))))

(defn about [templater router]
  (fn [req]
    (page templater router req
          (hiccup/html
           [:div
            [:h1.cover-heading "About"]
            [:p "Join the Google group: modularity"]]))))

;; Components are defined using defrecord.

(defrecord Website [templater router]

  ; modular.bidi provides a router which dispatches to routes provided
  ; by components that satisfy its WebService protocol
  WebService
  (request-handlers [this]
    ;; Return a map between some keywords and their associated Ring
    ;; handlers
    {::index (index templater router)
     ::features (features templater router)
     ::about (about templater router)})

  ;; Return a bidi route structure, mapping routes to keywords defined
  ;; above. This additional level of indirection means we can generate
  ;; hyperlinks from known keywords.
  (routes [_] ["/" {"index.html" ::index
                    "" (redirect ::index)
                    "features.html" ::features
                    "about.html" ::about}])

  ;; A WebService can be 'mounted' underneath a common uri context
  (uri-context [_] ""))

;; While not mandatory, it is common to use a function to construct an
;; instance of the component. This affords the opportunity to control
;; the construction with parameters, provide defaults and declare
;; dependency relationships with other components.

(defn new-website []
  (-> (map->Website {})
      (using [:templater])
      (co-using [:router])))
