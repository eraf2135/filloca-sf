(ns filloca-sf.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [filloca-sf.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[filloca-sf started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[filloca-sf has shut down successfully]=-"))
   :middleware wrap-dev})
