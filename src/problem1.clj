(ns problem1
  (:require [clojure.edn :as edn]))

(def invoice (edn/read-string (slurp "invoice.edn")))

(defn getValueTax
  [taxes]
  (get-in taxes [0 :tax/rate ]))

(defn getValueRetention
  [rets]
  (get-in rets [0 :retention/rate]))

(defn retValid?
  [item]
  (>= (or (getValueRetention (get item :retentionable/retentions)) 0) 1))

(defn taxValid?
  [item]
  (< (or (getValueTax (get item :taxable/taxes)) 21) 20))

(defn itemValid?
  [item]
  (and (= (getValueTax (get item :taxable/taxes)) 19)
    (= (getValueRetention (get item :retentionable/retentions)) 1)))

(defn isValid?
  "valid all conditions for accept an item"
  [item]
  (not (last (list (retValid? item) (taxValid? item) (itemValid? item)))))

(defn showInvoice
  "function to get the items without ->> operator"
  [file]
  (let [items (second (second file))]
       (reduce (fn [new-items item]
                 (if (isValid? item)
                   (conj new-items item)
                   new-items)
                 )
               []
               items)))

(def filterItems (fn [new-items item]
                   (if (isValid? item)
                     (conj new-items item)
                     new-items)
                   ))

(defn getItems
  "function to get items wit ->> operator"
  [file]
  (let [items (second (second file))]
    (->> items
         (reduce filterItems [])
         )))
