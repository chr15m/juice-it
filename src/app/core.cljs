(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defonce state (r/atom {}))

(defn move [x y & [attrs]]
  (assoc-in (or attrs {})
            [:style :translate]
            (str x "px " y "px")))

(defn component-float-tiles []
  (let [on (r/atom false)]
    (fn []
      [:div.card {:on-click #(swap! on not)}
       (when @on
         (for [x (range 3)
               y (range 2)]
           [:i.twa.twa-white-large-square.twa-5x.juicy__bubbleup
            (move (- (* x 100) 100) (- (* y 100) 50)
                  {:key [x y]
                   :class (str "juicy__bubbleup-" (inc (js/Math.floor (* (js/Math.random) 4))))
                   :style {:animation-delay (str (int (* (js/Math.random) 500)) "ms")}})]))])))

(defn component-screenshake []
  (let [on (r/atom false)]
    (fn []
      [:div.card {:on-click #(reset! on true)
                  :class (when @on "juicy__screenshake")
                  :on-animation-end #(reset! on false)}
       (if @on
         [:span
          [:i.twa.twa-grimacing-face.twa-5x (move -50 0)]
          [:i.twa.twa-grimacing-face.twa-5x (move 50 0)]]
         [:span
          [:i.twa.twa-grinning-face.twa-5x (move -50 0)]
          [:i.twa.twa-grinning-face-with-smiling-eyes.twa-5x (move 50 0)]])])))

(defn component-dash []
  (let [on (r/atom false)]
    (fn []
      [:div.card {:on-click #(reset! on true)}
       [:i.twa.twa-grinning-face.twa-5x (when @on {:class "juicy__zipright"})]
       (when @on
         [:i.twa.twa-dashing-away.twa-5x {:class "juicy__fade"
                                          :on-animation-end #(reset! on false)}])])))

(defn component-shake []
  (let [v (r/atom 0)]
    (fn []
      [:div.card {:on-click #(swap! v (fn [o] (mod (inc o) 5)))}
       [:svg.shadow (move 0 43 {:width 100 :height 100}) [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5}]]
       (case @v
         0 [:i.twa.twa-grinning-face.twa-5x]
         1 [:i.twa.twa-grinning-face-with-sweat.twa-5x {:class "juicy__shake__1"}]
         2 [:i.twa.twa-zany-face.twa-5x {:class "juicy__shake__2"}]
         3 [:i.twa.twa-grimacing-face.twa-5x {:class "juicy__shake__3"}]
         4 [:i.twa.twa-dizzy-face.twa-5x {:class "juicy__shake__4"}])])))

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
   [component-float-tiles]
   [component-screenshake]
   [component-dash]
   [component-shake]
   [component-bounce]])

(defn main []
  (print "hi"))

(rdom/render [component-main state]
             (js/document.getElementById "app"))
