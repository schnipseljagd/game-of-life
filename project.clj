(defproject game-of-life "0.1.0-SNAPSHOT"
  :description "Just another game of life implementation"
  :license {:name "MIT License"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [pandect "0.6.1"]
                 [quil "2.5.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.clojure/core.async "0.2.391" :exclusions [org.clojure/tools.reader]]
                 [cljs-http "0.1.42"]
                 [compojure "1.6.0-beta3" :exclusions [ring/ring-core]]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [environ "1.1.0"]
                 [spootnik/unilog "0.7.15"]]

  ;; package is used by the Dockerfile for the build
  :aliases {"package" ["do"]
                      ["test"]
                      ["uberjar"]}

  :plugins [[lein-figwheel "0.5.8"]
            [lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]
            [lein-environ "1.1.0"]]

  :source-paths ["src"]

  :target-path "target/%s/" ; don't get AOT in your REPL

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src"]

                ;; the presence of a :figwheel configuration here
                ;; will cause figwheel to inject the figwheel client
                ;; into your build
                :figwheel     {:on-jsload "game-of-life.quil/on-js-reload"
                               ;; :open-urls will pop open your application
                               ;; in the default browser once Figwheel has
                               ;; started and complied your application.
                               ;; Comment this out once it no longer serves you.
                               :open-urls ["http://localhost:3449/index.html"]}

                :compiler     {:main                 game-of-life.quil
                               :asset-path           "js/compiled/out"
                               :output-to            "resources/public/js/compiled/game_of_life.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :source-map-timestamp true
                               ;; To console.log CLJS data-structures make sure you enable devtools in Chrome
                               ;; https://github.com/binaryage/cljs-devtools
                               :preloads             [devtools.preload]}}
               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id           "min"
                :source-paths ["src"]
                :compiler     {:output-to     "resources/public/js/compiled/game_of_life.js"
                               :main          game-of-life.quil
                               :optimizations :advanced
                               :pretty-print  false}}]}

  :figwheel {;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"]}            ;; watch and update CSS

  ;; Start an nREPL server into the running figwheel process
  ;; :nrepl-port 7888

  ;; Server Ring Handler (optional)
  ;; if you want to embed a ring handler into the figwheel http-kit
  ;; server, this is for simple ring servers, if this

  ;; doesn't work for you just run your own server :) (see lein-ring)

  ;; :ring-handler hello_world.server/handler

  ;; To be able to open files in your editor from the heads up display
  ;; you will need to put a script on your path.
  ;; that script will have to take a file path and a line number
  ;; ie. in  ~/bin/myfile-opener
  ;; #! /bin/sh
  ;; emacsclient -n +$2 $1
  ;;
  ;; :open-file-command "myfile-opener"

  ;; if you are using emacsclient you can just use
  ;; :open-file-command "emacsclient"

  ;; if you want to disable the REPL
  ;; :repl false

  ;; to configure a different figwheel logfile path
  ;; :server-logfile "tmp/logs/figwheel-logfile.log"



  ;; setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl

  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [binaryage/devtools "0.8.2"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.0"]]
                   :source-paths ["src" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init             (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
             :uberjar {:prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                       :main game-of-life.webserver}})
