(ns bandits.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [reagent.core :as reagent]
   [bandits.subs :as subs]
   [bandits.events :as events]))

(def <sub (comp deref subscribe))

(defn num-input [{:keys [orig-val on-save on-stop]}] 
  (let [val  (reagent/atom orig-val)
        stop #(do (reset! val orig-val) 
                  (when on-stop (on-stop))) 
        save #(let [v @val]
                (on-save v)
                (stop))] 
    (fn [props] 
      [:input.input (merge (dissoc props :on-save :on-stop :orig-val)
                     {:type        "number"
                      :value       @val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(reset! val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      27 (stop)
                                      nil)})])))

(defn arm-row
  [index]
  (let [id (<sub [::subs/arm-id index])
        reward-prob (<sub [::subs/arm-reward-prob index])
        alpha (<sub [::subs/arm-alpha index])
        beta (<sub [::subs/arm-beta index])
        times-selected (<sub [::subs/arm-times-selected index])
        reward (<sub [::subs/arm-reward index])]
    [:tr
     [:th (inc id)]
     [:td [num-input {:orig-val reward-prob :on-save #(dispatch [::events/set-arm-reward-prob id %])}]]
     [:td [num-input {:orig-val alpha :on-save #(dispatch [::events/set-arm-alpha id %])}]]
     [:td [num-input {:orig-val beta :on-save #(dispatch [::events/set-arm-beta id %])}]]
     [:td times-selected]
     [:td reward]]))

(defn main-panel []
  [:table.table.has-text-centered.is-fullwidth
   [:thead
    [:tr
     [:th "Arm"]
     [:th "Reward Probability"]
     [:th "alpha"]
     [:th "beta"]
     [:th "Times Selected"]
     [:th "Cumulative Reward"]]]
   [:tbody
    (for [idx (range (<sub [::subs/num-arms]))]
      ^{:key idx} [arm-row idx])]])

