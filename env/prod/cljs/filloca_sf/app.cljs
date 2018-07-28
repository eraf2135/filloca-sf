(ns filloca-sf.app
  (:require [filloca-sf.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
