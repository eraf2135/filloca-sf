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
    :tags ["Endpoints"]

    (GET "/films" []
      :query-params [{title :- String ""}]
      :summary      "Films by name: limits 20"
      (ok (dsf/get-films dsf/data-sf 20 0 {:title title})))))
