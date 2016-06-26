(set-env!
  :resource-paths #{"src"}
  :source-paths   #{"tst"}
  :target-path      "tgt"
  :dependencies  '[[org.clojure/clojure             "1.7.0"  :scope "provided"]
                   [boot/core                       "2.3.0"  :scope "provided"]
                   [adzerk/bootlaces                "0.1.11" :scope "test"]
                   [adzerk/boot-test                "1.0.4"  :scope "test"]
                   [clj-http                        "2.0.0"  :scope "test"]
                   [ring/ring-mock                  "0.3.0"  :scope "test"]]
 :repositories  [["clojars"       "https://clojars.org/repo/"]
                 ["maven-central" "https://repo1.maven.org/maven2/"]])
(require
  '[adzerk.bootlaces          :refer :all]
  '[adzerk.boot-test          :refer [test]]
  '[tailrecursion.boot-static :refer [serve]])

(def +version+ "0.0.1-SNAPSHOT")

(bootlaces! +version+)

(replace-task!
  [t test] (fn [& xs] (comp (build-jar) (serve) (apply t xs))))

(deftask build []
  (comp (test) (build-jar)))

(deftask develop []
  (comp (watch) (speak) (test)))

(task-options!
  pom  {:project     'tailrecursion/boot-static
        :version     +version+
        :description "a boot task for serving static resources"
        :url         "https://github.com/tailrecursion/boot-static"
        :scm         {:url "https://github.com/tailrecursion/boot-static"}
        :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}}
  serve {:port       3006}
  test  {:namespaces #{'tailrecursion.boot-static-test}}
  web   {:serve      'tailrecursion.boot-static-app/serve})
