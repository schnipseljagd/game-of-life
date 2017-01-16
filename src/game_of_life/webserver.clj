(ns game-of-life.webserver
  (:gen-class)
  (:require [ring.util.response :refer [file-response content-type not-found]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [environ.core :as environ]))

(def app
  (-> (fn [request]
        (if (= "/" (:uri request))
          (-> (file-response "resources/public/index.html")
              (content-type "text/html"))
          (not-found "")))
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)))

(def server (atom nil))

(defn server-stop []
  (when-let [s @server]
    (.stop s)
    (.join s)
    (reset! server nil)))

(defn server-start []
  (server-stop)
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn -main []
  (server-start))
