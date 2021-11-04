(ns integration.rabbitmq-ansible.rabbitmq-definitions-to-yaml
  (:require [cheshire.core :as json]
            [clj-yaml.core :as yaml]))

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
   (retrieve_key_values_from_json json json_key fields)))

(defn edn_to_yaml [edn]
  (yaml/generate-string
    edn
    :dumper-options {:flow-style :block}))

(defn from_json_get_extra_vhosts [json]
  (from_json_get_extra_ json
                        "vhosts"
                        [:name]))

(defn from_json_get_extra_exchanges [json]
  (from_json_get_extra_ json
                        "exchanges"
                        [:name
                         :type
                         :vhost]))

(defn from_json_get_extra_bindings [json]
  (from_json_get_extra_ json
                        "bindings"
                        [:destination
                         :destination_type
                         :routing_key
                         :source
                         :vhost]))

(defn generate_ansible_yaml [json]
  (edn_to_yaml
    {:rabbitmq_extra_vhosts
    (from_json_get_extra_vhosts    json)
    :rabbitmq_extra_exchanges 
    (from_json_get_extra_exchanges json)
    :rabbitmq_extra_bindings
    (from_json_get_extra_bindings  json)}))

(defn generate_single_vhost_params_yaml [json vhost]
  (edn_to_yaml
    {:vhosts
    (let [vhosts (from_json_get_extra_vhosts json)]
     (filter (fn [x] (= (x :name) vhost)) vhosts))
    :exchanges
    (let [exchanges (from_json_get_extra_exchanges json)]
     (filter (fn [x] (= (x :vhost) vhost)) exchanges))
    :bindings
    (let [bindings (from_json_get_extra_bindings json)]
     (filter (fn [x] (= (x :vhost) vhost)) bindings))}))
