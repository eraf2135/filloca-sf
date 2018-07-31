(ns filloca-sf.map)

(def sf-map (atom nil))
(def current-layer-id (atom nil))

(defn mount [map-element-id]
  (reset! sf-map (js/mapboxgl.Map. (clj->js {:container map-element-id
                                            :style     "mapbox://styles/mapbox/streets-v10"
                                            :center    [-122.4194 37.7749]
                                            :zoom     6})))
  (.addControl @sf-map (mapboxgl.NavigationControl.)))

(defn- add-layer [m film-name geo-edn]
  (reset! current-layer-id film-name)
  (.addLayer m (clj->js {:id     film-name
                         :type   "symbol"
                         :source {:type "geojson"
                                  :data geo-edn}
                         :layout {:icon-image         "cinema-15"
                                  :icon-allow-overlap true}})))

(defn fly-to [feature]
  (.flyTo @sf-map (clj->js {:center (-> feature :geometry :coordinates)
                            :zoom 15
                            })))

(defn add-markers [film-name features]
  (let [features (filter identity features)
        geo-edn (assoc {:type "FeatureCollection"} :features features)]
    (when @current-layer-id
      (.removeLayer @sf-map @current-layer-id))
    (add-layer @sf-map film-name geo-edn)
    (fly-to (first features))))

