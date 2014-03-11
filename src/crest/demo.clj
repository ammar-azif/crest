(ns crest.demo
  (:import [org.restlet.data Method MediaType]
           [org.restlet.data Protocol])
  (:require [clojure.data.json :as json]
            [ring.util.codec])
  (:use [crest.core]))

(defn test-get1 [request-params request-data]
  (println request-params)
  (println "application1"))

(defn test-get2 [request-params request-data]
  (println request-params)
  (println "application2"))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  ;; Demonstrating component modularity using restlet
  (let [component (create-component "Testor"
                                    Protocol/HTTP 8111
                                    {"/app1" (create-app
                                              (create-router {"/lol" (create-restlet Method/GET test-get1)}))
                                     "/app2" (create-app
                                              (create-router {"/lol" (create-restlet Method/GET test-get2)}))})]
    (.start component)
    (GET "http://localhost:8111/app1/lol"
         :accepted-type MediaType/APPLICATION_JSON
         :query-params {"a" "1" "b" "2"})
    (GET "http://localhost:8111/app2/lol"
         :accepted-type MediaType/APPLICATION_JSON
         :query-params {"c" "3" "d" "4"})
    ))
