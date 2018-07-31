(ns filloca-sf.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [filloca-sf.boundaries.data-sf :as dsf]
            [filloca-sf.boundaries.mapbox :as mb]
            [clojure.string :as st]
            [clojure.tools.logging :as log]))

(def san-francisco-coords [-122.4194 37.7749])

(defn- safe-string [text]
  (st/replace text #"/" " "))

(defn- sort-by-distance [features]
  (sort-by (juxt (comp first :center)
                 (comp second :center))
           features))

(defn- get-closest-locations [text]
  (let [locations (mb/get-locations mb/mapbox (safe-string text) san-francisco-coords)]
    (first (sort-by-distance (:features locations)))))

(defn- get-geo-locations [descriptions]
  (if (string? descriptions)
    (get-closest-locations descriptions)
    (pmap get-closest-locations descriptions)))

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
                     {limit :- Long 10}
                     {offset :- Long 0}]
      :summary      "Films by name"
      (do
        ;todo: delete once dev is finished
        ;(defonce b (dsf/get-films dsf/data-sf (or limit Integer/MAX_VALUE) offset {:title title}))
        ;(ok b)
        (ok (dsf/get-films dsf/data-sf
                           limit
                           offset
                           {:title title}))))

    (GET "/geo-locations" []
      :query-params [desc]
      :summary      "Lat Long for a location description. Returns results with closest distance to San Fransisco"
      (ok (get-geo-locations desc)))))
