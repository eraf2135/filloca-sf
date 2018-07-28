(ns user
  (:require [filloca-sf.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [filloca-sf.figwheel :refer [start-fw stop-fw cljs]]
            [filloca-sf.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'filloca-sf.core/repl-server))

(defn stop []
  (mount/stop-except #'filloca-sf.core/repl-server))

(defn restart []
  (stop)
  (start))


