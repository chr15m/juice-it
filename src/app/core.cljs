(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defonce state
  (r/atom
    {:preloads ["elf-dark-skin-tone"
                "deer"
                "collision"
                "star"
                "man-mage-dark-skin-tone"
                "white-large-square"
                "grimacing-face"
                "grinning-face"
                "dashing-away"
                "grinning-face-with-sweat"
                "zany-face"
                "dizzy-face"]}))

(defn preload-twa-emoji [preloads p el]
  (when el
    (let [url (-> el
                  (js/window.getComputedStyle)
                  (aget "background-image")
                  (.replace "url(\"" "")
                  (.replace "\")" ""))
          i (js/Image.)]
      (aset i "onload" #(swap! preloads (fn [ps] (remove (partial = p) ps))))
      (aset i "src" url))))

(defn move [x y & [attrs]]
  (assoc-in (or attrs {})
            [:style :transform]
            (str "translate(" x "px," y "px)")))

(defn make-particle-attributes [n]
  (for [x (range n)]
    {:key (js/Math.random)
     :style {"--particle-jump" (+ (* (js/Math.random) 2) 0.5)
             "--particle-direction" (* (- (js/Math.random) 0.5) 2)
             "--particle-spin" (- (js/Math.random) 0.5)
             "--particle-size" (+ (* (js/Math.random) 0.5) 1.0)
             :animation-delay (str (* (js/Math.random) 0.5) "s")}}))

(defn component-attack []
  (let [on (r/atom false)
        p (r/atom [])]
    (fn []
      [:div
       [:div.title "Attack"]
       [:div.card {:on-click (fn [ev]
                               (swap! on not)
                               (js/setTimeout #(swap! p concat (make-particle-attributes 10)) 300)
                               (js/setTimeout #(swap! on not) 1500))}
        [:div (move 20 10)
         (for [i @p]
           [:div.juicy__particle
            (assoc i :on-animation-end #(swap! p (fn [ps] (remove (partial = i) ps))))
            [:i.twa.twa-collision.twa-5x]])]
        [:div (move -50 0) [:i.twa.twa-elf-dark-skin-tone.twa-5x {:class (when @on "juicy__attack-initiate")}]]
        [:div (move 50 0) [:i.twa.twa-deer.twa-5x {:class (when @on "juicy__attack-receive")}]]]])))

(defn component-particles []
  (let [p (r/atom [])
        q 20]
    (fn []
      [:div
       [:div.title "Particles" (when (not-empty @p) (str " (" (count @p) ")"))]
       [:div.card
        {:on-click
         #(swap! p concat (make-particle-attributes q))}
        [:div (move -18 -10)
         (for [i @p]
           [:div.juicy__particle
            (assoc i :on-animation-end #(swap! p (fn [ps] (remove (partial = i) ps))))
            [:i.twa.twa-star.twa-5x]])]
        [:i.twa.twa-man-mage-dark-skin-tone.twa-3x]]])))

(defn component-float-tiles []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:div.title "Bubble up tiles"]
       [:div.card {:on-click #(swap! on not)}
        [:span {:style {:transform "scaleY(0.66)"}}
         (when @on
           (for [x (range 3)
                 y (range 2)]
             [:div (move (- (* x 100) 100) (- (* y 100) 50) {:key [x y]})
              [:i.twa.twa-white-large-square.twa-5x.juicy__bubbleup
               {:key [x y]
                :class (str "juicy__bubbleup-" (inc (js/Math.floor (* (js/Math.random) 4))))
                :style {:animation-delay (str (int (* (js/Math.random) 500)) "ms")}}]]))]]])))

(defn component-screenshake []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:div.title "Screenshake"]
       [:div.card {:on-click #(reset! on true)
                   :class (when @on "juicy__screenshake")
                   :on-animation-end #(reset! on false)}
        [:svg.shadow (move -100 43 {:width 100 :height 100})
           [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5}]]
        [:svg.shadow (move 100 43 {:width 100 :height 100})
           [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5}]]
        (if @on
          [:span
           [:i.twa.twa-grimacing-face.twa-5x (move -100 0)]
           [:i.twa.twa-grimacing-face.twa-5x (move 100 0)]]
          [:span
           [:i.twa.twa-grinning-face.twa-5x (move -100 0)]
           [:i.twa.twa-grinning-face-with-smiling-eyes.twa-5x (move 100 0)]])]])))

(defn component-dash []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:div.title "Dash"]
       [:div.card {:on-click #(reset! on true)}
        (when (not @on)
          [:svg.shadow (move 0 43 {:width 100 :height 100})
           [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5}]])
        [:i.twa.twa-grinning-face.twa-5x (when @on {:class "juicy__zipright"})]
        (when @on
          [:i.twa.twa-dashing-away.twa-5x {:class "juicy__fade"
                                           :on-animation-end #(reset! on false)}])]])))

(defn component-shake []
  (let [v (r/atom 0)]
    (fn []
      [:div
       [:div.title "Shaker"]
       [:div.card {:on-click #(swap! v (fn [o] (mod (inc o) 5)))}
        (when (< @v 3)
          [:svg.shadow (move 0 43 {:width 100 :height 100})
           [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5}]])
        (case @v
          0 [:i.twa.twa-grinning-face.twa-5x]
          1 [:i.twa.twa-grinning-face-with-sweat.twa-5x {:class "juicy__shake__1"}]
          2 [:i.twa.twa-zany-face.twa-5x {:class "juicy__shake__2"}]
          3 [:i.twa.twa-grimacing-face.twa-5x {:class "juicy__shake__3"}]
          4 [:i.twa.twa-dizzy-face.twa-5x {:class "juicy__shake__4"}])]])))

(defn component-bounce []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:div.title "Bounce"]
       [:div.card
        {:on-click #(reset! on true)}
        [:svg.shadow (move 0 43 {:width 100 :height 100})
         [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5
                    :class (when @on "juicy__bounce__shadow")}]]
        (if @on
          [:i.twa.twa-zany-face.twa-5x {:class "juicy__bounce"
                                        :on-animation-end #(reset! on false)}]     
          [:i.twa.twa-grinning-face.twa-5x])]])))

(defn component-main [state]
  (let [preloads (r/cursor state [:preloads])]
    (if (not-empty @preloads)
      [:div
       [:div#loading [:i.twa.twa-skull-and-crossbones.twa-4x]]
       (for [p @preloads]
         [:i.twa.twa-5x {:key p
                         ;:style {:opacity 0}
                         :class (str "twa-" p)
                         :ref (partial preload-twa-emoji preloads p)}])]
      [:div
       [component-attack]
       [component-particles]
       [component-float-tiles]
       [component-screenshake]
       [component-dash]
       [component-shake]
       [component-bounce]])))

(defn main []
  (print "hi"))

(js/setTimeout
  #(rdom/render [component-main state]
                (js/document.getElementById "app"))
  500)
