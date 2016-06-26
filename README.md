# boot-static [![Build Status][1]][2]
a static web server for boot

[](dependency)
```clojure
[tailrecursion/boot-static "0.0.1-SNAPSHOT"] ;; latest release
```
[](/dependency)

## overview
this task uses Vert.X to create a standalone static webserver.  the server in this project will eventually be factored out and moved into a separate `boot-vertx` task. in this future configuration, the purpose of `boot-static` will to to inject an application into the build pipeline that serves static resources, while `boot-vertx` will be concerned with isolating the app in a pod then serving it.

## rationale
serving artifacts such as a single page app and its associated resources is a concern separate from the job of the webservice, which is responsible for returning information persisted in databases.  consequently, it's a good practice for the spa to live in its own project where boot orchestrates the compilation to a separate artifact to be served from its own origin.  this configuration, however, necessitates a local development server to approximate the behavior of the deployment environment (such as an s3 bucket backing cloudfront).  

you can currently accomplish the same thing using [`boot-jetty`](3), although you may find this task a bit snappier. this is the merely the first step along the path to a more ambitious undertaking that will utilize boot's pods and pod-pools to maintain a clean, interactive environment without the need for annoying jvm restarts or libraries that compromise lisp's ability to clearly express the logic of the problem your application is solving.

## usage
```clojure
(require
  '[adzerk.boot-cljs          :refer [cljs]]
  '[adzerk.boot-reload        :refer [reload]]
  '[hoplon.boot-hoplon        :refer [hoplon]]
  '[tailrecursion.boot-static :refer [serve]])

(deftask develop
  [o optimizations OPT kw "Optimization level of the closure compiler (:none :simple :advanced)"]
  (let [o (or optimizations :none)]
    (comp (watch) (speak) (hoplon) (reload) (cljs :optimizations o) (serve))))
```

## notes
when you're serving static content at an origin different from your webservice (such as a separate port on localhost), you'll also need to add some [cors middleware](4) to your server.  the same holds true in production, although you may want to consider uniting your different endpoints behind the same origin through a cdn such as cloudfront if you're using aws.

[1]: https://travis-ci.org/tailrecursion/boot-static.png?branch=app-pod
[2]: https://travis-ci.org/tailrecursion/boot-static
[3]: https://github.com/tailrecursion/boot-jetty
[4]: https://github.com/jumblerg/ring.middleware.cors
