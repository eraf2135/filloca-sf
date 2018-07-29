(ns filloca-sf.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx]]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [clojure.string :as s]
            [filloca-sf.map :as sf-map]))

;;dispatchers

(reg-event-db
  :navigate
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-film-search-val
  (fn [db [_ val]]
    (assoc db :film-search-val val)))

(reg-event-db
  :set-films
  (fn [db [_ films]]
    (assoc db :films films
              :loading-films? false)))

(reg-event-db
  :film-load-failed
  (fn [db [_]]
    (assoc db :films []
              :loading-films? false)))                      ;todo: handle failure

(reg-event-fx
  :get-films
  (fn [{:keys [db]} _]
    {:db         (assoc db :loading-films? true)
     :http-xhrio {:method          :get
                  :uri             "/api/filming-locations"
                  :params          {:limit 5000}            ;let get all upfront for faster autocomplete
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:set-films]
                  :on-failure      [:film-load-failed]}}))

(reg-event-fx
  :set-geo-locations
  (fn [{:keys [db]} [_ locations]]
    (sf-map/add-markers (:selected-film db) locations)
    {:db (assoc db :geo-locations locations
                   :loading-locations? false)}))

(reg-event-db
  :locations-load-failed
  (fn [db [_]]
    (assoc db :geo-locations []
              :loading-locations? false)))                      ;todo: handle failure

(defn locations-for-film [db]
  (filter #(= (:selected-film db) (:title %)) (:films db)))

(reg-event-fx
  :get-geolocations
  (fn [{:keys [db]} _]
    {:db         (assoc db :loading-locations? true)
     :http-xhrio {:method          :get
                  :uri             "/api/geo-locations"
                  :params          {:desc (map #(str (:locations %) " San Francisco") (locations-for-film db))}
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:set-geo-locations]
                  :on-failure      [:locations-load-failed]}}))

(reg-event-fx
  :set-selected-film
  (fn [{:keys [db]} [_ title]]
    {:db       (assoc db :selected-film title)
     :dispatch [:get-geolocations]}))

;;subscriptions

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
  :loading-films?
  (fn [db _]
    (:loading-films? db)))

(reg-sub
  :films
  (fn [db _]
    (:films db)))

(reg-sub
  :film-search-val
  (fn [db _]
    (:film-search-val db)))

(reg-sub
  :suggestions
  (fn [db _]
    (let [val (:film-search-val db)
          trimmed-val (if (string? val) (-> val s/trim s/lower-case) "")
          distinct-films (distinct (map :title (:films db)))]
      (filterv #(s/includes? (s/lower-case %) trimmed-val) distinct-films))))

(reg-sub
  :locations-for-film
  (fn [db _]
    (locations-for-film db)))

(reg-sub
  :selected-film
  (fn [db _]
    (:selected-film db)))