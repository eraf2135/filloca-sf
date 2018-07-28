(ns filloca-sf.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx]]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [clojure.string :as s]))

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
                  :uri             "/api/films"
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:set-films]
                  :on-failure      [:film-load-failed]}}))

(reg-event-db
  :selected-film
  (fn [db [_ title]]
    (js/console.log title)
    (assoc db :selected-film title)))

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
    (filter #(= (:selected-film db) (:title %)) (:films db))))