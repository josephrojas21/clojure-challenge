(ns invoice-spec
  (:require
    [clojure.spec.alpha :as s]
    [cheshire.core :as json]))

(defn transform-invoice [invoice-json]
  (let [invoice (:invoice invoice-json)
        customer (:customer invoice)
        items (:items invoice)
        retentions (:retentions invoice)]
    {:invoice/issue_date (:issue_date invoice)
     :invoice/payment_means_type (:payment_means_type invoice)
     :invoice/number (:number invoice)
     :invoice/order_reference (:order_reference invoice)
     :invoice/payment_date (:payment_date invoice)
     :invoice/payment_means (:payment_means invoice)
     :invoice/customer {:customer/company_name (:company_name customer)
                        :customer/email (:email customer)}
     :invoice/items (vec (map (fn [item]
                                {:invoice-item/price (:price item)
                                 :invoice-item/quantity (:quantity item)
                                 :invoice-item/sku (:sku item)
                                 :invoice-item/taxes (vec (map (fn [tax]
                                                                 {:tax/tax_category (-> tax :tax_category keyword)
                                                                  :tax/tax_rate (Double/parseDouble (str (:tax_rate tax)))
                                                                  })
                                                               (:taxes item)))
                                 })
                              items))
     :invoice/retentions (vec (map (fn [retention]
                                     {:tax/tax_category (-> retention :tax_category keyword)
                                      :tax/tax_rate (Double/parseDouble (str (:tax_rate retention)))
                                      })
                                   retentions))
     }))
(defn validate-invoice [filename]
  (let [json-str (slurp filename)
        invoice-map (json/parse-string json-str true)
        invoice (transform-invoice invoice-map)]
    (s/valid? ::invoice invoice)))

(validate-invoice "invoice.json")

(s/def :invoice/payment_date string?)
(s/def :invoice/payment_means string?)
(s/def :invoice/payment-means-type string?)
(s/def :invoice/order_reference string?)
(s/def :invoice/number string?)
(s/def :customer/company_name string?)
(s/def :customer/email string?)
(s/def :invoice/customer (s/keys :req [:customer/company_name
                                       :customer/email]))

(s/def :tax/tax_rate double?)
(s/def :tax/tax_category #{:RET_IVA, :RET_FUENTE, :IVA})
(s/def ::tax (s/keys :req [:tax/tax_category
                           :tax/tax_rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

(s/def :invoice/issue_date string?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def :invoice/retentions (s/coll-of ::tax :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/payment_means_type
                :invoice/number
                :invoice/issue_date
                :invoice/payment_date
                :invoice/customer
                :invoice/payment_means
                :invoice/items
                :invoice/retentions]))
