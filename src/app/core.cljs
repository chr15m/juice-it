(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defonce state (r/atom {}))

(defn move [x y attrs]
  (merge attrs {:style {:translate (str x "px " y "px")}}))

(defn component-screenshake []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:div.card {:on-click #(reset! on true)
                   :class (when @on "juicy__screenshake")
                   :on-animation-end #(reset! on false)}
        (if @on
          [:i.twa.twa-grimacing-face.twa-5x]
          [:i.twa.twa-grinning-face.twa-5x])]])))

(defn component-dash []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:div.card {:on-click #(reset! on true)}
        (if @on
          [:i.twa.twa-dashing-away.twa-5x {:class "juicy__fade"
                                           :on-animation-end #(reset! on false)}]
          [:i.twa.twa-grinning-face.twa-5x])]])))

(defn component-shake []
  (let [v (r/atom 0)]
    (fn []
      [:div
       [:div.card {:on-click #(swap! v (fn [o] (mod (inc o) 5)))}
        [:svg.shadow (move 0 43 {:width 100 :height 100}) [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5}]]
        (case @v
          0 [:i.twa.twa-grinning-face.twa-5x]
          1 [:i.twa.twa-grinning-face-with-sweat.twa-5x {:class "juicy__shake__1"}]
          2 [:i.twa.twa-zany-face.twa-5x {:class "juicy__shake__2"}]
          3 [:i.twa.twa-grimacing-face.twa-5x {:class "juicy__shake__3"}]
          4 [:i.twa.twa-dizzy-face.twa-5x {:class "juicy__shake__4"}])]])))

(defn component-bounce []
  (let [on (r/atom false)]
    (fn []
      [:div.card
       {:on-click #(reset! on true)}
       [:svg.shadow (move 0 43 {:width 100 :height 100}) [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5
                                                                    :class (when @on "juicy juicy__shadow__bounce")}]]
       (if @on
         [:i.twa.twa-zany-face.twa-5x {:class "juicy juicy__bounce"
                                       :on-animation-end #(reset! on false)}]     
         [:i.twa.twa-grinning-face.twa-5x])])))

(defn component-main [state]
  [:div 
   [component-screenshake]
   [component-dash]
   [component-shake]
   [component-bounce]])

(defn main []
  (print "hi"))

(rdom/render [component-main state]
             (js/document.getElementById "app"))
