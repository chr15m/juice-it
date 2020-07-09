(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defonce state (r/atom {}))

(defn move [x y attrs]
  (merge attrs {:style {:translate (str x "px " y "px")}}))

(defn component-card-click-bounce []
  (let [on (r/atom false)]
    (fn []
      [:div.card
       {:on-click #(reset! on true)}
       [:svg.shadow (move 0 43 {:width 100 :height 100}) [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5
                                                                    :class (when @on "animate__animated animate__shadow__bounce")}]]
       (if @on
         [:i.twa.twa-zany-face.twa-5x {:class "animate__animated animate__bounce"
                                       :on-animation-end #(reset! on false)}]     
         [:i.twa.twa-grinning-face.twa-5x])])))

(defn component-main [state]
  [component-card-click-bounce]
  )

(defn main []
  (print "hi"))

(rdom/render [component-main state]
             (js/document.getElementById "app"))
