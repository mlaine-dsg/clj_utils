(ns integration.clj-utils-test
  (:require [clojure.test :refer :all]
            [clj-yaml.core :as yaml] 
            [integration.clj-utils :refer :all]))

(def rabbitmq_definitions_json
  (slurp "./resources/update_ansible_config/test/rabbitmq_definitions.json"))

(def expected_rabbitmq_definitions_ansible_output
  (slurp "./resources/update_ansible_config/test/rabbitmq_definitions_ansible_output.yaml"))

(def rabbitmq_definitions_ansible_output
  (yaml/generate-string
   {:rabbitmq_extra_vhosts
    (integration.clj-utils/from_json_get_extra_vhosts rabbitmq_definitions_json)
    :rabbitmq_extra_exchanges
    (integration.clj-utils/from_json_get_extra_exchanges rabbitmq_definitions_json)
    :rabbitmq_extra_bindings
    (integration.clj-utils/from_json_get_extra_bindings rabbitmq_definitions_json)}
   :dumper-options {:flow-style :block}))

(deftest update_ansible_config_use_case
  (testing "This test is for converting rabbitmq definitions json output to yaml usible by ansible to replicate necessary structures in a cluster"
    (is (= expected_rabbitmq_definitions_ansible_output
           rabbitmq_definitions_ansible_output))))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 0))))

