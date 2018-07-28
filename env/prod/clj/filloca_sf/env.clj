(ns filloca-sf.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[filloca-sf started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[filloca-sf has shut down successfully]=-"))
   :middleware identity})
