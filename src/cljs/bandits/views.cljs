(ns bandits.views
  (:require
   [re-frame.core :refer [subscribe dispatch dispatch-sync]]
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
      [:input.input.has-text-centered (merge (dissoc props :on-save :on-stop :orig-val)
                     {:type        "number"
                      :read-only   (<sub [::subs/has-started?])
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
        reward (:reward arm)
        selected? (= id (<sub [::subs/selected-arm]))]
    [:tr
     (when selected? {:class "is-selected"})
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
   [:tfoot
    [:tr
     [:th "Totals"]
     [:td]
     [:td]
     [:td]
     [:td (<sub [::subs/time-step])]
     [:td (<sub [::subs/total-reward])]]]
   [:tbody
    (for [arm (<sub [::subs/arms])]
      ^{:key (:id arm)} [row arm])]])

(defn control-button
  []
  (let [state (<sub [::subs/state])]
    [:button.button.is-primary {:on-click #(dispatch [::events/start-sim])
                                :disabled (= state :running)}
     "Start"]))

(defn pause-button
  []
  (let [state (<sub [::subs/state])]
    [:button.button.is-warning {:on-click #(dispatch [::events/pause-sim])
                                :disabled (= state :paused)}
     "Pause"]))

(defn reset-button
  []
  (let [state (<sub [::subs/state])]
    [:button.button.is-danger {:on-click #(dispatch-sync [::events/reset-sim])}
     "Reset"]))

(defn main-panel []
  [:div.container
   [main-table]
   [:div.buttons
    [control-button]
    [pause-button]
    [reset-button]]])

