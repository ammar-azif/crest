(ns crest.restlet-wrapper
  (:import [org.restlet.data Method MediaType]
           [org.restlet Restlet Component Application]
           [org.restlet.routing Router]
           [org.restlet.data Status Protocol]
           [org.restlet.resource ClientResource ResourceException])
  (:require [clojure.data.json :as json]
            [ring.util.codec]))


(defn convert-request-params [request]
  (if-let [query-string (.. (.getResourceRef request) (getQuery))]
    (ring.util.codec/form-decode query-string)))

(defn convert-request-body [request]
  ;Getting text from request consumes memory, not good when the data is big
  (let [entity (.getEntity request)
        media-type (.getMediaType entity)
        entity-body (.getText entity)]
    (cond
     (= media-type MediaType/APPLICATION_JSON) (json/read-str entity-body)
     :else entity-body)))

(defn convert-response-body [body media-type]
  (cond
   (= media-type MediaType/APPLICATION_JSON) (json/write-str body)
   :else body))

(defn handler-wrapper [handler request response]
  (let [request-params (convert-request-params request)
        request-body (convert-request-body request)
        client-accepted-media-type (.getMetadata (first (.. (.getClientInfo request)
                                                            (getAcceptedMediaTypes))))
        result (handler request-params request-body)]
    (.setEntity response
                (convert-response-body result
                                       client-accepted-media-type)
                client-accepted-media-type)))

(defn create-restlet [& {:as handlers}]
  (proxy [Restlet] []
    (handle [request response]
      (if-let [handler (handlers (.getMethod request))]
        (handler-wrapper handler request response)
        (throw (ResourceException.
                Status/CLIENT_ERROR_METHOD_NOT_ALLOWED))))))

(defn create-router [routes]
  (let [router (Router.)]
    (doseq [[url restlet] routes]
      (.attach router url restlet))
    router))

(defn create-app [router]
  (proxy [Application] []
    (createInboundRoot []
      router)))

(defn create-client-resource [uri method query-params]
  (let [client-resource (ClientResource. method uri)]
    (if query-params
      (doseq [[key value] query-params]
        (.addQueryParameter client-resource key value)))
    client-resource))

(defn create-component [name protocol port applications]
  (let [component (Component.)
        default-host (.getDefaultHost component)]
    (.setName component name)
    (.. (.getServers component) (add protocol port))
    (doseq [[uri application] applications]
      (.attach default-host uri application))
    component))

;; (defn GET
;;   [uri & {:keys [accepted-type query-params]
;;           :or {accepted-type MediaType/TEXT_PLAIN}}]
;;   (let [client-resource (create-client-resource uri Method/GET query-params)
;;         representation (.get client-resource accepted-type)]
;;     (cond
;;      (= accepted-type MediaType/APPLICATION_JSON) (json/read-str (.getText representation))
;;      :else (.getText representation))))

;; (defn POST [uri data & {:keys [accepted-type query-params]
;;                         :or {accepted-type MediaType/TEXT_PLAIN}}]
;;   (let [client-resource (create-client-resource uri Method/POST query-params)
;;         representation (.post client-resource data accepted-type)]
;;     (cond
;;      (= accepted-type MediaType/APPLICATION_JSON) (json/read-str (.getText representation))
;;      :else (.getText representation))))


(defmacro generate-client-resource-fn [call-name]
  (let [[client-resource-method-name method] (cond (= call-name 'GET) ['.get 'Method/GET]
                                                   (= call-name 'POST) ['.post 'Method/POST])
        keyword-args {:keys ['accepted-type 'query-params] :or {'accepted-type 'MediaType/TEXT_PLAIN}}]
    (list
     'defn call-name
     (if (= call-name 'GET)
       ['uri '& keyword-args]
       ['uri 'data '& keyword-args])
     (list 'let ['client-resource (list 'create-client-resource 'uri method 'query-params)
                 'representation (if (= call-name 'GET)
                                   (list client-resource-method-name 'client-resource 'accepted-type)
                                   (list client-resource-method-name 'client-resource 'data 'accepted-type))]
           (list 'cond '(= accepted-type MediaType/APPLICATION_JSON) '(json/read-str (.getText representation))
                 :else '(.getText representation))))))


(generate-client-resource-fn GET)
(generate-client-resource-fn POST)






