(ns problem1
  (:require [clojure.edn :as edn]))

(def invoice (edn/read-string (slurp "invoice.edn")))

(defn satisfies-conditions? [item]
  (let [has-iva? (some #(= (:tax/category %) :iva) (:taxable/taxes item))
        has-retention? (some #(= (:retention/category %) :ret_fuente) (:retentionable/retentions item))]
    (and (or has-iva? has-retention?)
         (not (and has-iva? has-retention?)))))


(defn get-invoice-items [invoice]
  (->> invoice
       :invoice/items
       (filter satisfies-conditions?)))

(get-invoice-items invoice)

