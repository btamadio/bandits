(ns bandits.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::state
 (fn [db]
   (:state db)))

(reg-sub
 ::method
 (fn [db]
   (:method db)))

(reg-sub
 ::arms
 (fn [db]
   (:arms db)))

(reg-sub
 ::arm-selections
 :<- [::arms]
 (fn [arms [_ _]]
   (into [] (map :times-selected arms))))

(reg-sub
 ::arm-probs
 :<- [::arms]
 (fn [arms [_ _]]
   (into [] (map :reward-prob arms))))

(reg-sub
 ::arm-rewards
 :<- [::arms]
 (fn [arms _]
   (into [] (map :reward arms))))

(reg-sub
 ::num-arms
 :<- [::arms]
 (fn [arms [_ _]]
   (count arms)))

(reg-sub
 ::arm
 :<- [::arms]
 (fn [arms [_ index]]
   (arms index)))

(reg-sub
 ::arm-id
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :id])))

(reg-sub
 ::arm-reward-prob
 :<- [::arm-probs]
 (fn [arm-probs [_ index]]
   (arm-probs index)))

(reg-sub
 ::arm-alpha
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :alpha])))

(reg-sub
 ::arm-beta
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :beta])))

(reg-sub
 ::arm-times-selected
 :<- [::arm-selections]
 (fn [arm-selections [_ index]]
   (arm-selections index)))

(reg-sub
 ::time-step
 :<- [::arm-selections]
 (fn [arm-selections _]
   (reduce + arm-selections)))

(reg-sub
 ::total-reward
 :<- [::arm-rewards]
 (fn [arm-rewards _]
   (reduce + arm-rewards)))

(reg-sub
 ::arm-reward
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :reward])))

(reg-sub
 ::samples
 (fn [db]
   (:samples db)))

(reg-sub
 ::selected-arm
 (fn [db]
   (:selected-arm db)))

(reg-sub
 ::simulated-reward
 (fn [db]
   (:simulated-reward db)))
