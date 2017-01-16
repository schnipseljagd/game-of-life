(ns game-of-life.quil
  (:require [game-of-life.game-of-life :refer [step init-world]]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn run [size living]
  (q/defsketch gol
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
       (map #(Integer/parseInt %))))


(comment
  ;; period-51 oscillator
  ;; http://www.conwaylife.com/wiki/112P51
  (spit "src/game_of_life/game_of_life/p112p51_106.lif"
        (slurp "http://www.conwaylife.com/patterns/112p51_106.lif"))
  (def p112p51_106 (slurp "src/game_of_life/game_of_life/p112p51_106.lif"))
  (->> p112p51_106
       (clojure.string/split-lines)
       (rest)
       (map line->coords)
       (map (partial map (partial + 38)))
       (run 400)))


