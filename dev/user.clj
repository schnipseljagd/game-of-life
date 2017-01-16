(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
    [clojure.repl :refer (apropos dir doc find-doc pst source)]
    [clojure.test :as test]
    [clojure.tools.namespace.repl :refer (refresh refresh-all)]
    [figwheel-sidecar.repl-api :as f]
    [game-of-life.webserver :as webserver]))

(defmacro dbg [& body]
  (let [result (gensym 'result)]
    `(let [~result ~@body]
       (prn ~result)
       ~result)))

(defn fig-start
  "This starts the figwheel server and watch based auto-compiler."
  []
  ;; this call will only work are long as your :cljsbuild and
  ;; :figwheel configurations are at the top level of your project.clj
  ;; and are not spread across different lein profiles

  ;; otherwise you can pass a configuration into start-figwheel! manually
  (f/start-figwheel!))

(defn fig-stop
  "Stop the figwheel server and watch based auto-compiler."
  []
  (f/stop-figwheel!))

;; if you are in an nREPL environment you will need to make sure you
;; have setup piggieback for this to work
(defn cljs-repl
  "Launch a ClojureScript REPL that is connected to your build and host environment."
  []
  (f/cljs-repl))

(defn go
  []
  (webserver/server-start)
  :ready)

(defn reset
  []
  (webserver/server-stop)
  (refresh :after 'user/go))
