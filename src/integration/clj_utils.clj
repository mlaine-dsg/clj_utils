(ns integration.clj-utils)

(require '[cheshire.core :as json])

(defn transmogrify_fields_for_ansible
  ([param]
   (transmogrify_fields_for_ansible param [:name]))
  ([param fields]
   (merge
    (zipmap fields
            (map (fn [field] (get param (name field))) fields))
    {:state "present"})))

(defn retrieve_key_values_from_json
  ([json_string json_key fields]
   (let [json (json/parse-string json_string)]
   (->> (get json (name json_key))
        (map #(transmogrify_fields_for_ansible % fields))))))

(defn from_json_get_extra_
  ([json json_key]
   (from_json_get_extra_ json json_key [:name]))
  ([json json_key fields]
   (retrieve_key_values_from_json json json_key fields))
  ([json json_key fields except_names]
   (->> (from_json_get_extra_ json json_key fields)
        (filter #(let [iname (% :name)]
                   (every? (fn [x] (not= iname x))
                           except_names))))))

