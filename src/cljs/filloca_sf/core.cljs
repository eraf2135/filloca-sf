(ns filloca-sf.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [ajax.core :refer [GET POST]]
            [filloca-sf.ajax :refer [load-interceptors!]]
            [filloca-sf.events]
            [secretary.core :as secretary]
            [filloca-sf.home :as home])
  (:import goog.History))

(defn nav-link [uri title page]
  [:li.nav-item
   {:class (when (= page @(rf/subscribe [:page])) "active")}
   [:a.nav-link {:href uri} title]])

(defn navbar []
  [:nav.navbar.navbar-dark.bg-secondary.navbar-expand-md
   {:role "navigation"}
   [:button.navbar-toggler.hidden-sm-up
    {:type        "button"
     :data-toggle "collapse"
     :data-target "#collapsing-navbar"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand {:href "#/"} "Filloca SF"]
   [:div#collapsing-navbar.collapse.navbar-collapse
    [:ul.nav.navbar-nav.mr-auto
     [nav-link "#/" "Home" :home]
     [nav-link "#/about" "About" :about]]]])

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(def pages
  {:home  #'home/page
   :about #'about-page})

(defn mapbox-map []
  (r/create-class
    {:component-did-mount (fn []
                            (let [m (js/mapboxgl.Map. (clj->js {:container "map"
                                                                :style "mapbox://styles/mapbox/streets-v10"
                                                                :center [-122 38]
                                                                :zoom 6
                                                                }))]

                              (.addControl m (mapboxgl.NavigationControl.))))
     :reagent-render      (fn []
                            [:div#map {:style {:width  "500px"
                                               :height "400px"}}])}))

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]
   [mapbox-map]
   ])

;; -------------------------
;; Routes

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (rf/dispatch [:navigate :home]))

(secretary/defroute "/about" []
                    (rf/dispatch [:navigate :about]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:navigate :home])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components)
  (rf/dispatch [:get-films]))
