(ns invoice-item-test
  (:require [clojure.test :refer [deftest is]]
            [invoice-item :as item]))


(deftest test-subtotal-without-discount
  (is (= 100.0 (item/subtotal {:invoice-item/precise-quantity 1.0 :invoice-item/precise-price 100.0}))))

(deftest test-subtotal-with-discount
  (is (= 80.0 (item/subtotal {:invoice-item/precise-quantity 1.0 :invoice-item/precise-price 100.0 :invoice-item/discount-rate 20}))))

(deftest test-subtotal-with-zero-quantity
  (is (= 0.0 (item/subtotal {:invoice-item/precise-quantity 0.0 :invoice-item/precise-price 100.0 :invoice-item/discount-rate 20}))))

(deftest test-subtotal-with-zero-price
  (is (= 0.0 (item/subtotal {:invoice-item/precise-quantity 1.0 :invoice-item/precise-price 0.0 :invoice-item/discount-rate 20}))))

(deftest test-subtotal-with-zero-discount-rate
  (is (= 100.0 (item/subtotal {:invoice-item/precise-quantity 1.0 :invoice-item/precise-price 100.0 :invoice-item/discount-rate 0}))))