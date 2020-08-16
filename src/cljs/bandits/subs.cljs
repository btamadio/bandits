(ns bandits.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::stage
 (fn [db]
   (:stage db)))

(re-frame/reg-sub
 ::state
 (fn [db]
   (:state db)))

(re-frame/reg-sub
 ::method
 (fn [db]
   (:method db)))

(re-frame/reg-sub
 ::arms
 (fn [db]
   (:arms db)))

(re-frame/reg-sub
 ::num-arms
 :<- [::arms]
 (fn [arms [_ _]]
   (count arms)))

(re-frame/reg-sub
 ::arm
 :<- [::arms]
 (fn [arms [_ index]]
   (arms index)))

(re-frame/reg-sub
 ::arm-id
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :id])))

(re-frame/reg-sub
 ::arm-reward-prob
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :reward-prob])))

(re-frame/reg-sub
 ::arm-alpha
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :alpha])))

(re-frame/reg-sub
 ::arm-beta
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :beta])))

(re-frame/reg-sub
 ::arm-times-selected
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :times-selected])))

(re-frame/reg-sub
 ::arm-reward
 :<- [::arms]
 (fn [arms [_ index]]
   (get-in arms [index :reward])))

(re-frame/reg-sub
 ::samples
 (fn [db]
   (:samples db)))

(re-frame/reg-sub
 ::selected-arm
 (fn [db]
   (:selected-arm db)))

(re-frame/reg-sub
 ::simulated-reward
 (fn [db]
   (:simulated-reward db)))
