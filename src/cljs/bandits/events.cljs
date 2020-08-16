(ns bandits.events
  (:require
   [re-frame.core :refer [reg-event-db
                          reg-event-fx
                          reg-cofx
                          reg-fx
                          inject-cofx
                          dispatch]]
   [bandits.db :as db]
   ["jstat" :as jstat]))

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-db
 ::set-arm-alpha
 (fn [db [_ index val]]
   (assoc-in db [:arms index :alpha] val)))

(reg-event-db
 ::set-arm-beta
 (fn [db [_ index val]]
   (assoc-in db [:arms index :beta] val)))

(reg-event-db
 ::set-arm-reward-prob
 (fn [db [_ index val]]
   (assoc-in db [:arms index :reward-prob] val)))

(defn draw-from-beta
  [{:keys [:alpha :beta]}]
  (jstat/beta.sample alpha beta))

(defn draw-from-bernoulli
  [p]
  (let [r (rand)]
    (< r p)))

(reg-cofx
 ::random-samples
 (fn [coeffects _]
   (let [arms (get-in coeffects [:db :arms])
         samples (map draw-from-beta arms)]
     (assoc-in coeffects [:db :samples] (into [] samples)))))

; (beware: always returns max index on tie)
(defn argmax
  [coll]
  (first (apply max-key second (map vector (range) coll))))

(reg-cofx
 ::select-arm
 (fn [coeffects _]
   (let [samples (get-in coeffects [:db :samples])]
     (assoc-in coeffects [:db :selected-arm] (argmax samples)))))

(reg-cofx
 ::simulate-reward
 (fn [coeffects _]
   (let [arm-id (get-in coeffects [:db :selected-arm])
         reward-prob (get-in coeffects [:db :arms arm-id :reward-prob])]
     (assoc-in coeffects [:db :reward?] (draw-from-bernoulli reward-prob)))))

(reg-event-db
 ::tick
 [(inject-cofx ::random-samples) (inject-cofx ::select-arm) (inject-cofx ::simulate-reward)]
 (fn [db _]
   (let [reward? (:reward? db)
         arm-id (:selected-arm db)]
     (-> db
         (update-in [:arms arm-id (if reward? :alpha :beta)] inc)
         (update-in [:arms arm-id :times-selected] inc)
         (update-in [:arms arm-id :reward] (if reward? inc identity))))))

(def registered-keys (atom nil))

(reg-fx
 ::dispatch-interval
 (fn [{:keys [:event :ms :id]}]
   (let [interval-id (js/setInterval #(dispatch event) ms)]
     (swap! registered-keys assoc id interval-id))))

(reg-fx
 ::clear-interval
 (fn [{:keys [:id]}]
   (when-let [interval-id (get @registered-keys id)]
     (js/clearInterval interval-id)
     (swap! registered-keys dissoc id))))

(reg-event-fx
 ::start-sim
 (fn [cofx _]
   (let [db (:db cofx)]
     {:db (assoc db :state :running)
      ::dispatch-interval {:event [::tick]
                           :id :ticker
                           :ms 1000}})))

(reg-event-fx
 ::pause-sim
 (fn [cofx _]
   (let [db (:db cofx)]
     {:db (assoc db :state :paused)
      ::clear-interval {:id :ticker}})))

(defn reset-arm
  [arm]
  (-> arm
      (assoc :alpha 1)
      (assoc :beta 1)
      (assoc :times-selected 0)
      (assoc :reward 0)))

(reg-event-db
 ::reset-sim
 (fn [db _]
   (update-in db [:arms] #(into [] (map reset-arm) %))))
