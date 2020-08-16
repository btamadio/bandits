(ns bandits.db)

(def default-db
  {:stage :pre-sim
   :method :classical
   :arms [{:id 0 :reward-prob 0.5 :alpha 1 :beta 1 :times-selected 0 :reward 0}
          {:id 1 :reward-prob 0.5 :alpha 1 :beta 1 :times-selected 0 :reward 0}
          {:id 2 :reward-prob 0.5 :alpha 1 :beta 1 :times-selected 0 :reward 0}]
   :samples []
   :selected-arm nil
   :reward? nil})

