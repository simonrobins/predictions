(ns predictions.routes.home
  (:require
   [predictions.layout :as layout]
   [predictions.db.core :as db]
   [predictions.middleware :as middleware]
   [struct.core :as st]
   [ring.util.response]
   [ring.util.http-response :as response]))

(def user-schema
  [[:name st/required st/string]
   [:initials st/required st/string]])

(defn validate-user [params]
  (first (st/validate params user-schema)))

(defn insert-user! [{:keys [params]}]
  (if-let [errors (validate-user params)]
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/insert-user! params)
      (response/found "/"))))

(defn users-page [{:keys [flash] :as request}]
  (let [errors (select-keys flash [:name :initials :errors])
        users {:users (db/get-users)}
        values (merge users errors)]
    (layout/render request "users" values)))

(defn teams-page [{:keys [flash] :as request}]
  (let [errors (select-keys flash [:name :initials :errors])
        teams {:teams (db/get-teams)}
        values (merge teams errors)]
    (layout/render request "teams" values)))

(defn fixtures-page [{:keys [flash] :as request}]
  (let [errors (select-keys flash [:name :initials :errors])
        fixtures {:fixtures (db/get-fixtures)}
        values (merge fixtures errors)]
    (layout/render request "fixtures" values)))

(defn home-routes []
  [ "" {:middleware [middleware/wrap-csrf middleware/wrap-formats]}
   [["/users"    {:get users-page :post insert-user!}]]
   [["/teams"    {:get teams-page}]]
   ["/fixtures" {:get fixtures-page}]])