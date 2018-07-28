(ns filloca-sf.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [filloca-sf.core-test]))

(doo-tests 'filloca-sf.core-test)

