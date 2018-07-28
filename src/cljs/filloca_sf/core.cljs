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

(def pages
  {:home  #'home/page})

(defn page []
  [:div
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (rf/dispatch [:navigate :home]))

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
