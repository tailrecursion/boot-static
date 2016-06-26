(ns tailrecursion.boot-static
  {:boot/export-tasks true}
  (:require
    [clojure.set :as set]
    [boot.pod    :as pod]
    [boot.core   :as boot]
    [boot.util   :as util])
  (:import
    [java.nio.file Paths]))

(def ^:private deps
  '[[io.vertx/vertx-core    "3.2.1"]
    [io.vertx/vertx-rx-java "3.2.1"]
    [io.vertx/vertx-web     "3.2.1"]])

(defn- warn-deps [deps]
  (let [conflict (delay (util/warn "Overriding Vert.x dependencies, using:\n"))]
    (doseq [dep deps]
      (when (pod/dependency-loaded? dep)
        @conflict
        (util/warn "• %s\n" (pr-str dep))))))

(defn- pod-env [deps]
  (let [dep-syms (->> deps (map first) set)]
    (warn-deps deps)
    (-> (dissoc pod/env :source-paths)
        (update-in [:dependencies] #(remove (comp dep-syms first) %))
        (update-in [:dependencies] into deps))))

(defn rel-path [tmp-dir]
  (let [path #(Paths/get % (into-array String []))]
    (str (.relativize (.toAbsolutePath (path "")) (path (.getPath tmp-dir))))))

(boot/deftask serve
  "Serve the application, refreshing the application with each subsequent invocation."
  [p port PORT int "The port the server will bind to."]
  (let [webapp  (boot/tmp-dir!)
        srv-pod (atom nil)
        message #(util/info "%s Vert.x on port %s...\n" % port)
        sync!   #(apply boot/sync! %1 (boot/output-dirs %2))
        start   (delay
                  (message "Starting")
                  (reset! srv-pod (pod/make-pod (pod-env deps)))
                  (pod/with-call-in @srv-pod
                    (tailrecursion.boot-static.impl/initialize! ~(rel-path webapp) ~port)))
        stop    (delay
                  (message "\nStopping")
                  (pod/with-call-in @srv-pod
                    (tailrecursion.boot-static.impl/terminate!)))]
    (boot/cleanup @stop)
    (boot/with-pre-wrap fileset
      (util/with-let [_ fileset]
        (sync! webapp fileset)
        @start))))
