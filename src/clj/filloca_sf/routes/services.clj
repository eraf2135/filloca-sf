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

    (GET "/filming-locations" []
      :query-params [{title :- String ""}
                     {limit :- Long nil}
                     {offset :- Long 0}]
      :summary      "Films by name"
      (do
        (defonce b (dsf/get-films dsf/data-sf (or limit Integer/MAX_VALUE) offset {:title title}))
        (ok b))
      #_(ok filming-locations))))
