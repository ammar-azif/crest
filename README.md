# crest

crest is a restlet framework http://restlet.org wrapper written in clojure. Restlet is a framework that supports the REST concepts covering both the client and the server side in a uniform interface. Restlet makes it easy for applications using RESTful service to communicate with each other. crest adds abstraction on top of restlet api in an attempt to make it easy to use the framework without having to learn much about it.   

## Installation

Download from https://github.com/lucialink/chloe.

## Usage

An example component declaration and its applications contained within.
```
(ns crest.demo
 (:import [org.restlet.data Method MediaType]
          [org.restlet.data Protocol])
 (:use [crest.core]))

(defn fn1 [request-params request-data]
   "fn1")
(defn fn2 [request-params request-data]
   "fn2")
(defn fn3 [request-params request-data]
   "fn3")

(let [component (create-component "ComponentX"     ;Component containing 2 applications
                                  Protocol/HTTP
                                  8111
                                  {"/app1" (create-app    ; app1 has 2 routes
                                             (create-router {"/get1" (create-restlet Method/GET fn1
                                                             "/get2" (create-restlet Method/GET fn2}))
                                  ; app2 has 1 route
                                   "/app2" (create-app    ; app2 has 1 routes
                                             (create-router {"/get1" (create-restlet Method/GET fn3)}))]
  (.start component)
  ; client code
  (println (GET "http://localhost:8111/app1/get1")) ; value returned by fn1 will be shown
  (println (GET "http://localhost:8111/app1/get2")) ; value returned by fn2 will be shown
  (println (GET "http://localhost:8111/app2/get1")) ; value returned by fn3 will be shown
```
  
                                          
  

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
