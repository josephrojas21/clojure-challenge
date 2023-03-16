(ns invoice-spec
  (:require
    [clojure.spec.alpha :as s])
  (:require
    [cheshire.core :as json])
  (:require
    [clojure.spec.gen.alpha :as gen]))

(defn genInvoice
  [n]
  (gen/sample (s/gen ::invoice) n))

(defn validate-invoice [file-name]
  (let [invoice (json/parse-string (slurp file-name) true)]
    (s/valid? ::invoice [invoice])))

; What I had in mind was to take the json data and first convert the data from string
; to date where necessary and then convert each key of the data to the format that receives
; the invoice definition.

;(defn validate-invoice-in-progress [file-name]
;  (let [invoice (json/parse-string (slurp file-name) true)]
;    (reduce (fn [new-invoice [key val]]
;              (assoc new-invoice (set/rename-keys {key val} {key (str ":invoice/" (clojure.string/replace key #"^:" ""))}) val)
;              )
;            {}
;            (:invoice invoice))))

(s/def :customer/company_name string?)
(s/def :customer/email string?)
(s/def :invoice/customer (s/keys :req [:customer/company_name
                                       :customer/email]))

(s/def :tax/rate double?)
(s/def :tax/category #{:iva})
(s/def ::tax (s/keys :req [:tax/category
                           :tax/rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

(s/def :invoice/issue_date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/issue_date]))

