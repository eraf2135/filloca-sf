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
                  :inputProps                  {:placeholder "Movie name"
                                                :value       (or @(rf/subscribe [:film-search-val]) "")
                                                :onChange    (fn [evt new-val method]
                                                               (rf/dispatch [:set-film-search-val (.-newValue new-val)]))}}]))

(defn- films-autocomplete []
  (r/create-class
    {:component-did-mount (fn []
                            (rf/dispatch [:get-films]))
     :reagent-render      (fn []
                            (if @(rf/subscribe [:loading-films?])
                              [:div "loading..."]
                              [:div
                               [auto-suggest "films"]
                               [:div (map #(vector :p (str %)) @(rf/subscribe [:locations-for-film]))]]))}))

(defn page []
  [:div.container
   [:p "Welcome!"]
   [:p "Search for movies filmed in the San Franciso Area and will show you the filming locations!"]
   [films-autocomplete]])