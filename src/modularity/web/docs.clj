(ns modularity.web.docs
  (:require
   [bidi.ring :refer (redirect)]
   [modularity.web.website :refer (page)]
   [modular.bidi :refer (WebService)]
   faker.lorem
   [hiccup.core :as hiccup]
   [com.stuartsierra.component :refer (using)]
   [tangrammer.component.co-dependency :refer (co-using)]
   ))

(defn docs [templater router]
  (fn [req]
    (page templater router req
          (hiccup/html
           [:div
            [:h1.cover-heading "Docs"]
            [:p (take 2 (faker.lorem/sentences))]]))))

(defrecord Website [templater router]

  ; modular.bidi provides a router which dispatches to routes provided
  ; by components that satisfy its WebService protocol
  WebService
  (request-handlers [this]
    ;; Return a map between some keywords and their associated Ring
    ;; handlers
    {::docs (docs templater router)})

  ;; Return a bidi route structure, mapping routes to keywords defined
  ;; above. This additional level of indirection means we can generate
  ;; hyperlinks from known keywords.
  (routes [_] ["" {"/index.html" ::docs
                    "/" (redirect ::docs)
                    "" (redirect ::docs)}])

  ;; A WebService can be 'mounted' underneath a common uri context
  (uri-context [_] "/docs"))

(defn new-docs-website []
  (-> (map->Website {})
      (using [:templater])
      (co-using [:router])))
