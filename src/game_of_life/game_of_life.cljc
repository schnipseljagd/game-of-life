(ns game-of-life.game-of-life.game-of-life)


;; queries the worlds state

(defn in-world? [world coords]
  (and (empty? (filter neg? coords))
       (> (:x-size world) (first coords))
       (> (:y-size world) (second coords))))

(defn neighbours [world coords]
  (let [surrounding-coords (for [x (range -1 2)
                                 y (range -1 2)
                                 :let [c [(+ x (first coords))
                                          (+ y (second coords))]]
                                 :when (not (= coords c))]
                             c)]
    (filter (partial in-world? world) surrounding-coords)))

(defn alive? [world coords]
  (some? (some (partial = coords) (:living world))))

(defn count-living-neighbours [world coords]
  (count (filter #(alive? world %) (neighbours world coords))))

(defn will-stay-alive? [world coords]
  (let [number-of-living-neighbours (count-living-neighbours world coords)]
    (or (= 2 number-of-living-neighbours)
        (= 3 number-of-living-neighbours))))

(defn will-become-alive? [world coords]
  (= 3 (count-living-neighbours world coords)))


;; generates the next world

(defn set-living [world living]
  (assoc world :living living))

(defn step [world]
  (let [known (distinct (mapcat (partial neighbours world) (:living world)))
        living (filter #(if (alive? world %)
                          (will-stay-alive? world %)
                          (will-become-alive? world %))
                       known)]
    (set-living world living)))

(defn init-world [size living]
  {:y-size size
   :x-size size
   :living living})

(comment
  (take 5 (iterate game-of-life.game-of-life.game-of-life/step
                   (init-world 100 [[0 0] [1 0] [2 0]]))))
