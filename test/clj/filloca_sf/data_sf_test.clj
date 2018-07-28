(ns filloca-sf.data-sf-test
  (:require [clojure.test :refer :all]
            [filloca-sf.data-sf :as dsf]))

(deftest soql-param-conversion
  (testing "Converts keywords to strings with underscore and does a 'like' filter"
    (is (= (#'dsf/to-soql :production-company "The Best Company")
           "production_company like '%The Best Company%'"))))

(deftest where-clause-test
  (testing "Actor checks all 3 actor fields"
    (is (= (#'dsf/->where-clause {:actor "John"})
           "(actor_1 like '%John%' or actor_2 like '%John%' or actor_3 like '%John%')")))
  (testing "Joins with logical AND"
    (is (= (#'dsf/->where-clause {:production-company "The Best Company" :writer "John"})
           "(actor_1 like '%%' or actor_2 like '%%' or actor_3 like '%%') and production_company like '%The Best Company%' and writer like '%John%'")))
  (testing "Release date used = int value"
    (is (= (#'dsf/->where-clause {:release-date 2000})
           "(actor_1 like '%%' or actor_2 like '%%' or actor_3 like '%%') and release_date = 2000"))))