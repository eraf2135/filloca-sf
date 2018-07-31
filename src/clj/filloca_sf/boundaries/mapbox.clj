(ns filloca-sf.boundaries.mapbox
  (:require [mount.core :as mount]
            [filloca-sf.config :refer [env]]
            [clj-http.client :as client]
            [org.tobereplaced.lettercase :as l]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]))

(defn- get-locations*
  [api-key text proximity-coords]
  (log/info "Getting locations for" text)
  (-> (client/get (format "https://api.mapbox.com/geocoding/v5/mapbox.places/%s.json" text)
                  {:query-params {:access_token api-key
                                  :country      "us"
                                  :types        "poi,poi.landmark,place,neighborhood"
                                  :proximity    (clojure.string/join "," proximity-coords)}})
      :body
      (json/read-str :key-fn l/lower-hyphen-keyword)))

(defprotocol Mapbox
  (get-locations [this text proximity-coords]
    "Gets possible locations and their details for a given text string."))

(defrecord MapboxImpl [api-key]
  Mapbox
  (get-locations [_ text proximity-coords]
    (get-locations* api-key text proximity-coords)))

(mount/defstate mapbox
                :start
                (map->MapboxImpl {:api-key (env :mapbox-api-key)}))

(comment
  ;todo: delete once finished dev

  (defn abs [x]
    (if (< x 0) (* -1 x) x))

  (defn dist [x feature]
    (-> feature :center first (- x) abs))

  (let [mi (map->MapboxImpl {:api-key (env :mapbox-api-key)})
        long -122.4194
        lat 37.7749
        coll (get-locations mi "Powell from Bush and Sutter" [long lat])]
    (clojure.pprint/print-table (map #(select-keys % [:center :relevance :text])
                                     (sort-by (juxt (comp first :center)
                                                    (comp second :center))
                                              (:features coll))))

    (clojure.pprint/print-table (map #(select-keys % [:center :relevance :text])
                                     (sort-by (juxt #(dist long %)
                                                    #(dist lat %))
                                              (:features coll))))))


