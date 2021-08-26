(ns predictions.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [predictions.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[predictions started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[predictions has shut down successfully]=-"))
   :middleware wrap-dev})
