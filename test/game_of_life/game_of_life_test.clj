(ns game-of-life.game-of-life.game-of-life-test
  (:require [clojure.test :refer :all]
            [game-of-life.game-of-life.game-of-life :refer :all]))

(def a-world (init-world 3 [[0 1]]))

(deftest test-neighbours
  (testing "returns all possible neighbour coordinates"
    (is (= [[0 1] [1 0] [1 1]]
           (neighbours a-world [0 0])))
    (is (= [[0 0] [0 1] [0 2] [1 0] [1 2] [2 0] [2 1] [2 2]]
           (neighbours a-world [1 1])))
    (is (= [[1 1] [1 2] [2 1]]
           (neighbours a-world [2 2])))))

(deftest test-in-world?
  (testing "that the coordinates are in the size of the world"
    (is (true? (in-world? a-world [0 0])))
    (is (true? (in-world? a-world [1 0])))
    (is (true? (in-world? a-world [0 1])))
    (is (true? (in-world? a-world [1 1])))
    (is (false? (in-world? a-world [-1 0])))
    (is (false? (in-world? a-world [0 -1])))
    (is (false? (in-world? a-world [3 0])))
    (is (false? (in-world? a-world [0 3])))))

(deftest test-alive?
  (testing "that the cell is alive"
    (is (true? (alive? a-world [0 1]))))
  (testing "that the cell is dead"
    (is (false? (alive? a-world [1 1])))))

(deftest test-living-neighbours
  (testing "returns the number of living neighbour cells"
    (let [world (set-living a-world [[0 1] [1 1]])]
      (is (= 2 (count-living-neighbours world [0 0])))
      (is (= 1 (count-living-neighbours world [1 1]))))
    (is (= 0 (count-living-neighbours a-world [0 1])))))

(deftest test-will-stay-alive?
  (testing "will stay alive because it has to living neighbours"
    (let [world (set-living a-world [[0 1] [1 1]])]
      (is (true? (will-stay-alive? world [0 0])))))
  (testing "will die because it has less than two living neighbours"
    (let [world (set-living a-world [[1 1]])]
      (is (false? (will-stay-alive? world [1 0])))))
  (testing "will die because it has more than three living neighbours"
    (let [world (set-living a-world [[1 1] [0 0] [2 1] [2 0]])]
      (is (false? (will-stay-alive? world [1 0]))))))

(deftest test-will-become-alive?
  (testing "will become alive because it has three living neighbours"
    (let [world (set-living a-world [[0 1] [1 1] [1 0]])]
      (is (true? (will-become-alive? world [0 0])))))
  (testing "will stay dead because it has only two living neighbours"
    (let [world (set-living a-world [[0 1] [1 0]])]
      (is (false? (will-become-alive? world [0 0]))))))

(deftest test-step
  (testing "returns no living cells"
    (is (= [] (:living (step a-world)))))
  (testing "returns one newly born and three that stayed alive"
    (let [world (set-living a-world [[0 1] [1 1] [1 0]])]
      (is (= [[0 0] [1 0] [1 1] [0 1]]
             (:living (step world))))))
  (testing "returns one newly born and one that stayed alive"
    (let [world (set-living a-world [[0 2] [1 2] [2 2]])]
      (is (= [[1 1] [1 2]]
             (:living (step world)))))))
