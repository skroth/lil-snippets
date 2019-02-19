(ns hft.core
  (:gen-class)
  (:require [clojure.math.combinatorics :as combo]
   :use [clojure.test :only [is deftest run-tests]]))

(def max-int java.lang.Integer/MAX_VALUE)
(def min-int java.lang.Integer/MIN_VALUE)

; three sum without combinations
(defn three-sum [v]
  (let [n (count v)]
    (distinct
      (for [i (range n)
            j (range (inc i) n)
            k (range (inc j) n)
            :let [trip [(nth v i) (nth v j) (nth v k)]]
            :when (= (apply + trip) 0)]
        (sort trip)))))

; three sum with combinations
(defn three-sum2 [v]
  (->>
    (combo/combinations v 3)
    (map sort)
    (distinct)
    (filter (fn [trip] (= (apply + trip) 0)))))

; same as above but without the threading macro
; (defn three-sum2 [v]
;   (filter (fn [trip] (= (apply + trip) 0))
;     (distinct
;       (map sort
;         (combo/combinations v 3)))))


; dvide two integers
(defn abs [x]
  (if (< x 0)
    (- 0 x)
    x))

(defn same-sign [x y]
  (or
    (and (pos? x) (pos? y))
    (and (neg? x) (neg? y))))

(defn divide [x y]
  ; clojure can handle larger numbers but per the problem spec
  ; we will only handle 32bit ints
  (if (or (< x min-int) (> x max-int) (< y min-int) (> y max-int))
    min-int

    (let [divisee (abs x)
          divisor (abs y)]
      (loop [i 0
             curr (- divisee divisor)]
        ; bail if we got the result or the divisor is 0
        (if (or (neg? curr) (= divisor 0))
          ; cool, we got the result, now check if we should return neg or not
          (if (same-sign x y)
            i
            (- 0 i))  ; flip the sign of the return value

          ; not at the result yet, so recur to the top of the loop
          (recur (inc i) (- curr divisor)))))))

; tests
(deftest three-sums
  (let [small-test [-1 0 1 2 -1 -4]]
    (is
      (=
        (set (three-sum small-test))
        (set (three-sum2 small-test))
        #{'(-1 0 1) '(-1 -1 2)}))))

(deftest div-tests
  (is (= (divide -100 10) -10))
  (is (= (divide 100 -11) -9))
  (is (= (divide -9 -3) 3))
  (is (= (divide 20 13) 1))
  (is (= (divide 20 21) 0))
  (is (= (divide 2147483648 1) -2147483648)))

(defn -main
  "HFT week 1 solutions"
  [& args]

  ; compare performance of the three-sum fns and then run the tests
  (let [test-ints (repeatedly 500 #(* (- 1 (* 2 (rand-int 2))) (rand-int 50)))]
    (println "Timing three-sum...")
    (time (doall (repeatedly 10000 #(three-sum test-ints))))

    (println "Timing three-sum2...")
    (time (doall (repeatedly 10000 #(three-sum2 test-ints))))

    (run-tests 'hft.core)))
