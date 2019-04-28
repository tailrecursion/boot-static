(ns tailrecursion.boot-static.impl
  (:import
   [io.vertx.core            Vertx Handler]
   [io.vertx.ext.web         Router]
   [io.vertx.ext.web.handler StaticHandler]))

(def server (atom nil))

(defn static-handler [vertx webroot path]
  (reify Handler
    (handle [_ req]
      (let [router (Router/router vertx)]
         (.handler (.route router path) (StaticHandler/create webroot))
         (.accept router req)))))

(defn- start [webroot port path]
  (let [vertx (Vertx/vertx)]
    (-> (.createHttpServer vertx)
        (.requestHandler (static-handler vertx webroot path))
        (.listen port))))

(defn initialize! [webroot port]
  (reset! server (start webroot port "/*")))

(defn terminate! []
  (.close @server))
