(defproject crest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.restlet.jse/org.restlet "2.1.7"]
                 [org.restlet.jse/org.restlet.ext.xml "2.1.7"]
                 [org.restlet.jse/org.restlet.ext.json "2.1.7"]
                 [org.clojure/data.json "0.2.4"]
                 [ring/ring-core "1.2.1"]]
  :repositories [["maven-restlet" "http://maven.restlet.org"]]
  :main crest.demo)
