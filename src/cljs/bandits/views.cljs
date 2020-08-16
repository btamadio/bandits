(ns bandits.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [reagent.core :as reagent]
   [bandits.subs :as subs]
   [bandits.events :as events]))

(def <sub (comp deref subscribe))

; Numeric input: saves on blur or pressing enter. press escape to revert to last saved value
(defn num-input [{:keys [value on-save on-stop]}] 
  (let [val  (reagent/atom value)
        prev-val (reagent/atom value)
        stop #(do (reset! val @prev-val) 
                  (when on-stop (on-stop)))
        save #(do (reset! prev-val @val)
                  (on-save @val))]
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

(defn row
  [arm]
  (let [id (:id arm)
        reward-prob (:reward-prob arm)
        alpha (:alpha arm)
        beta (:beta arm)
        times-selected (:times-selected arm)
        reward (:reward arm)]
    [:tr
     [:th (inc id)]
     [:td [num-input {:value reward-prob :on-save #(dispatch [::events/set-arm-reward-prob id %])}]]
     [:td alpha]
     [:td beta]
     [:td times-selected]
     [:td reward]]))

(defn main-table
  []
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
    (for [arm (<sub [::subs/arms])]
      ^{:key (:id arm)} [row arm])]])

(defn control-button
  []
  [:button.button.is-primary
   {:on-click #(dispatch [::events/tick])}
   "Step"])

(defn main-panel []
  [:div.container
   [main-table]
   [control-button]])

