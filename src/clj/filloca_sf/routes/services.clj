(ns filloca-sf.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [filloca-sf.data-sf :as dsf]))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Filloca API"
                           :description "Services"}}}}
  
  (context "/api" []
    :tags ["AJAX Endpoints"]

    (GET "/films" []
      :query-params [{title :- String ""}
                     {limit :- Integer 20}
                     {offset :- Integer 0}]
      :summary      "Films by name"
      ;(def a (dsf/get-films dsf/data-sf limit offset {:title title}))
      ;(ok a)
      (ok (dsf/get-films dsf/data-sf Integer/MAX_VALUE offset {:title title})))))
