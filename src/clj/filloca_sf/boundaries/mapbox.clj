(ns filloca-sf.boundaries.mapbox
  (:require [mount.core :as mount]
            [filloca-sf.config :refer [env]]
            [clj-http.client :as client]
            [org.tobereplaced.lettercase :as l]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]))

(defn- get-locations*
  [api-key text]
  (log/info "Getting locations for" text)
  (-> (client/get (format "https://api.mapbox.com/geocoding/v5/mapbox.places/%s.json" text)
                  {:query-params {:access_token api-key}})
      :body
      (json/read-str :key-fn l/lower-hyphen-keyword)))

(defprotocol Mapbox
  (get-locations [this text]
    "Gets possible locations and their details for a given text string."))

(defrecord MapboxImpl [api-key]
  Mapbox
  (get-locations [_ text]
    (get-locations* api-key text)))

(mount/defstate mapbox
                :start
                (map->MapboxImpl {:api-key (env :mapbox-api-key)}))

(comment
  (let [mi (map->MapboxImpl {:api-key (env :mapbox-api-key)})]
    (get-locations mi "California @ Montgomery")))


