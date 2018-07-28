(ns filloca-sf.home
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :as rf]
            [clojure.string :as str]
            cljsjs.react-autosuggest))

(defn get-suggestion-value [suggestion]
  suggestion)

(defn render-suggestion [suggestion]
  (r/as-element
    [:span suggestion]))

(def Autosuggest (r/adapt-react-class js/Autosuggest))

(defn auto-suggest [id]
  (fn [id]
    [Autosuggest {:id                          id
                  :suggestions                 @(rf/subscribe [:suggestions])
                  :onSuggestionsFetchRequested #(rf/dispatch [:set-film-search-val (.-value %)])
                  :onSuggestionsClearRequested #(rf/dispatch [:set-film-search-val ""])
                  :onSuggestionSelected        (fn [evt o]
                                                 (rf/dispatch [:selected-film (.-suggestion o)]))
                  :getSuggestionValue          get-suggestion-value
                  :renderSuggestion            render-suggestion
                  :inputProps                  {:placeholder "Movie (e.g. Age of Adaline)"
                                                :value       (or @(rf/subscribe [:film-search-val]) "")
                                                :onChange    (fn [evt new-val method]
                                                               (rf/dispatch [:set-film-search-val (.-newValue new-val)]))}}]))

(defn- films-autocomplete []
  [auto-suggest "films"])

(defn- mapbox-map []
  (r/create-class
    {:component-did-mount (fn []
                            (let [m (js/mapboxgl.Map. (clj->js {:container "map"
                                                                :style     "mapbox://styles/mapbox/streets-v10"
                                                                :center    [-122 38]
                                                                :zoom      6
                                                                }))]

                              (.addControl m (mapboxgl.NavigationControl.))))
     :reagent-render      (fn []
                            [:div.row
                             [:div.col-md-3
                              [films-autocomplete]
                              [:br]
                              (when-let [f @(rf/subscribe [:selected-film])]
                                [:div
                                 [:h4 f]
                                 [:br]
                                 [:h5 "Filming Locations:"]
                                 [:div
                                  (map #(vector :p.filming-location-label (:locations %))
                                       @(rf/subscribe [:locations-for-film]))]])]
                             [:div#map {:class "map col-md-9"}]])}))

(defn page []
  [:div.container
   [:h2 "Filloca SF"]
   [:p "Welcome!" [:br]
    "Search for movies filmed in the San Franciso Area and will show you the filming locations!"]
   (if @(rf/subscribe [:loading-films?])
     [:div
      [:div.loader]
      "Loading..."]
     [mapbox-map])])