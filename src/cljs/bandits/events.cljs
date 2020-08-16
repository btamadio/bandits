(ns bandits.events
  (:require
   [re-frame.core :as re-frame]
   [bandits.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::set-arm-alpha
 (fn [db [_ index val]]
   (assoc-in db [:arms index :alpha] val)))
