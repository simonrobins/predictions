(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [predictions.config :refer [env]]
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [predictions.core :refer [start-app]]
    [predictions.db.core]
    [conman.core :as conman]
    [luminus-migrations.core :as migrations]
    [clojure.string :as str]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'predictions.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'predictions.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))

(defn restart-db
  "Restarts database."
  []
  (mount/stop #'predictions.db.core/*db*)
  (mount/start #'predictions.db.core/*db*)
  (binding [*ns* (the-ns 'predictions.db.core)]
    (conman/bind-connection predictions.db.core/*db* "sql/queries.sql")))

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate
  "Migrates database up for all outstanding migrations."
  []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback
  "Rollback latest database migration."
  []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration
  "Create a new up and down migration file with a generated timestamp and `name`."
  [name]
  (migrations/create name (select-keys env [:database-url])))

(defn- find-or-create-team [team]
  (if-let [id (:id (predictions.db.core/find-team {:name team}))]
    id
    (predictions.db.core/insert-team! {:name team})))

(defn create-fixture [line]
  (let [[date home away] line
        home-id (find-or-create-team home)
        away-id (find-or-create-team away)]
    (predictions.db.core/insert-fixture! {:home-id home-id :away-id away-id})))

(defn read-file [file]
  (slurp file))

(defn split-on-comma [lines]
  (map #(str/split % #",") lines))

; Separate line into date, home, away
; Find home & away in teams creating each one if not found
; Insert date, home_id, away_id in fixtures
(defn populate []
  (map create-fixture (split-on-comma (str/split-lines (read-file "resources/fixtures.csv")))))