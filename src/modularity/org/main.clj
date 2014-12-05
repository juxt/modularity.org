(ns modularity.org.main
  "Main entry point"
  (:require clojure.pprint)
  (:gen-class))

(defn -main [& args]
  ;; We eval so that we don't AOT anything beyond this class
  (eval '(do (require 'modularity.org.system)
             (require 'modularity.org.main)
             (require 'com.stuartsierra.component)
             (require 'tangrammer.component.co-dependency)

             (require 'clojure.java.browse)

             (println "Starting modularity.org")

             (let [system (->
                           (modularity.org.system/new-production-system)
                           tangrammer.component.co-dependency/start-system)]

               (println "System started")
               (println "Ready...")

               (let [url (format "http://localhost:%d/" (-> system :http-listener-listener :port))]
                 (println (format "Browsing at %s" url))
                 (clojure.java.browse/browse-url url))))))
