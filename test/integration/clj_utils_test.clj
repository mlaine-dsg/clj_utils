(ns integration.clj-utils-test
  (:require [clojure.test :refer :all]
            [integration.clj-utils :refer :all]))

(def rabbitmq_definitions_json
  (slurp "./resources/update_ansible_config/test/rabbitmq_definitions.json"))

(def expected_rabbitmq_definitions_ansible_yaml
  (slurp "./resources/update_ansible_config/test/expected_rabbitmq_definitions_ansible.yaml"))

(def actual_rabbitmq_definitions_ansible_yaml
    (integration.clj-utils/generate_ansible_yaml rabbitmq_definitions_json))

(def expected_canary_testing_yaml
  (slurp "./resources/update_ansible_config/test/canary_testing_vhost.yaml"))

(def actual_canary_testing_yaml
  (integration.clj-utils/generate_single_vhost_params_yaml
    rabbitmq_definitions_json
    "canary_testing"))

(deftest rabbitmq_definitions_ansible_use_cases
  (testing "This test suite is for converting RabbitMQ definitions json output to yaml usable by Ansible to replicate necessary structures in a cluster"
    (testing "All definitions -> params for all vhosts, exchanges and queues"
      (is (= expected_rabbitmq_definitions_ansible_yaml
             actual_rabbitmq_definitions_ansible_yaml)))
    (testing "All definitions + vhost -> params for single vhost, i.e. its exchanges and queues"
      (is (= expected_canary_testing_yaml
             actual_canary_testing_yaml)))))
