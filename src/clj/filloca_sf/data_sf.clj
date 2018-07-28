(ns filloca-sf.data-sf
  (:require [mount.core :as mount]
            [filloca-sf.config :refer [env]]
            [clj-http.client :as client]
            [org.tobereplaced.lettercase :as l]
            [clojure.string :as string]
            [clojure.set :as cs]
            [clojure.data.json :as json]))

;todo: handle case insensitivity

(defn- actor-soql [a]
  (str "(actor_1 like '%" a "%' or actor_2 like '%" a "%' or actor_3 like '%" a "%')"))

(defn ->like-clause [v]
  (str "like '%" v "%'"))

(defn- to-soql [k v]
  (str (l/lower-underscore-name k)
       " "
       (if (int? v)
         (str "= " v)
         (->like-clause v))))

;this is not used anymore because it's overkill. The data set is so small better to just load it all into browser so autocomplete and UX interations are faster.
(defn- ->where-clause [{:keys [actor] :as params}]
  (let [non-actor-where-clause (string/join " and "
                                            (map #(apply to-soql %)
                                                 (dissoc params :actor)))]
    (if (string/blank? non-actor-where-clause)
      (actor-soql actor)
      (format "%s and %s" (actor-soql actor) non-actor-where-clause))))

(defn- get-films*
  [api-key limit offset params]
  {:pre [(cs/subset? (set (keys params)) #{:actor :director :production-company :release-year :locations :title :writer})]}
  (when-let [w (->where-clause params)]
    (-> (client/get "https://data.sfgov.org/resource/wwmu-gmzc.json"
                    {:query-params (merge {:$limit (or limit 100)}
                                          {:$offset (or offset 0)}
                                          (when-let [w (->where-clause params)]
                                            {:$where w}))})
        :body
        (json/read-str :key-fn l/lower-hyphen-keyword))))

(defprotocol DataSF
  (get-films [this limit offset params]
    "Gets films from DataSF API. Only supports logical OR operations on filter params\n
    Supported params: actor director production-company release-year locations title writer"))

(defrecord DataSFImpl [api-key]
  DataSF
  (get-films [_ limit offset params]
    (get-films* api-key limit offset params)))

(mount/defstate data-sf
                :start
                (map->DataSFImpl {:api-key (env :api-key)}))

(comment
  (let [dsf (map->DataSFImpl {})] ;don't actually need an API key if willing to use shared pool...
    (count (get-films dsf 100 0 {
                                 ;:actor "John"
                                 ;:writer "Sam"
                                 ;:release-year 2017
                                 ;:title "About a Boy"
                                 }))))