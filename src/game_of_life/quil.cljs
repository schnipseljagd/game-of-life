(ns game-of-life.game-of-life.quil
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [game-of-life.game-of-life.game-of-life :refer [step init-world]]
            [quil.core :as q]
            [quil.middleware :as m]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(enable-console-print!)

(defn run [size living]
  (q/defsketch gol
               :host "canvas-id"
               :size [size size]
               :setup (fn []
                        (q/frame-rate 10)
                        (q/color-mode :hsb)
                        (init-world size living))
               :draw (fn [state]
                       (q/background 240)                   ; background color
                       (q/stroke 220)                       ; cell border color
                       (q/fill 180)                         ; cell body color

                       (doseq [[x y] (:living state)
                               :let [w 5]]
                         (q/rect (* w x) (* w y) w w 2)))

               :update (fn [state]
                         (step state))

               :middleware [m/fun-mode]))

(defn line->coords [line]
  (->> (clojure.string/split line #" ")
       (map #(js/parseInt %))))

(def absolute-path (-> js/window .-location .-href))

;; period-51 oscillator
;; http://www.conwaylife.com/wiki/112P51
(go (let [p112p51_106 (:body (<! (http/get (str absolute-path "p112p51_106.lif"))))]
      (->> p112p51_106
           (clojure.string/split-lines)
           (rest)
           (map line->coords)
           (map (partial map (partial + 38)))
           (run 400))))


