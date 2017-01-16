(ns game-of-life.webserver
  (:gen-class)
  (:require [ring.util.response :refer [resource-response content-type]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [environ.core :as environ]
            [game-of-life.middleware :refer [wrap-logging wrap-request-id]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [game-of-life.logging :refer [stop-logging start-logging]]))

(defn index [_]
  (-> (resource-response "index.html" {:root "public"})
      (content-type "text/html")))

(defroutes app-routes
           (GET "/" [] index)
           (GET "/healthcheck" [] "OK")
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-logging)
      (wrap-request-id)))

(def server (atom nil))

(defn server-stop []
  (when-let [s @server]
    (stop-logging)
    (.stop s)
    (.join s)
    (reset! server nil)))

(defn server-start []
  (start-logging)
  (server-stop)
  (reset! server (run-jetty app {:port 8080 :join? false})))

(defn -main []
  (server-start))
