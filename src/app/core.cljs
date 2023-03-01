(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defonce state
  (r/atom
    {:preloads ["cloud"
                "elf-dark-skin-tone"
                "ogre"
                "collision"
                "star"
                "coin"
                "fire"
                "blue-circle"
                "man-mage-dark-skin-tone"
                "white-large-square"
                "grimacing-face"
                "grinning-face"
                "dashing-away"
                "grinning-face-with-sweat"
                "zany-face"
                "knocked-out-face"]}))

(defn preload-twa-emoji [preloads p el]
  (when el
    (let [url (-> el
                  (js/window.getComputedStyle)
                  (aget "background-image")
                  (.replace "url(\"" "")
                  (.replace "\")" ""))
          i (js/Image.)
          rm #(swap! preloads (fn [ps] (remove (partial = p) ps)))]
      (if url
        (do
          (aset i "onload" rm)
          (aset i "onerror" rm)
          (aset i "src" url))
        (rm)))))

(defn move [x y & [attrs]]
  (assoc-in (or attrs {})
            [:style :transform]
            (str "translate(" x "px," y "px)")))

(defn make-particle-attributes [n]
  (for [_ (range n)]
    {:key (js/Math.random)
     :style {"--particle-jump" (+ (* (js/Math.random) 2) 0.5)
             "--particle-direction" (* (- (js/Math.random) 0.5) 2)
             "--particle-spin" (- (js/Math.random) 0.5)
             "--particle-size" (+ (* (js/Math.random) 0.5) 1.0)
             :animation-delay (str (* (js/Math.random) 0.5) "s")}}))

;*** animation cards ***;

(defn component-bubble []
  (let [p (r/atom [])]
    (fn []
      [:div
       [:span.about
        [:div.title "Bubbles"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L496-L532"} "css"]]
       [:div.card
        {:on-click (fn [ev]
                     (let [bounds (-> ev .-currentTarget .getBoundingClientRect)
                           c [(/ (+ (.-right bounds) (.-left bounds)) 2)
                              (/ (+ (.-top bounds) (.-bottom bounds)) 2)]
                           x (- (.-clientX ev) (first c))
                           y (- (.-clientY ev) (second c))]
                       (swap! p conj {:key (str (js/Math.random))
                                      :pos [x y]
                                      :style
                                      {"--bubble-height-scale" (+ (js/Math.random) 0.5)
                                       "--bubble-size-scale" (+ (js/Math.random) 0.5)
                                       "--bubble-width-scale" (* (- (js/Math.random) 0.5) 2)}})))}
        (for [s @p]
          [:div (move (-> s :pos first) (-> s :pos second) {:key (s :key)})
           [:div.juicy__bubble
            (assoc s :on-animation-end #(swap! p (fn [ps] (remove (partial = s) ps))))
            [:i.twa.twa-blue-circle.twa-2x]]])]])))

(defn component-smoke []
  [:div
   [:span.about
    [:div.title "Smoke"]
    [:a.link {:target "_blank"
              :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L458-L494"} "css"]]
   [:div.card
    (for [s (range 10)]
      [:div.juicy__smokepuff {:key s
                              :style
                              {"--smoke-duration-scale" (+ (js/Math.random) 0.5)
                               "--smoke-height-scale" (+ (js/Math.random) 0.5)
                               "--smoke-size-scale" (+ (js/Math.random) 0.5)
                               "--smoke-width-scale" (* (- (js/Math.random) 0.5) 2)
                               "--smoke-delay" (js/Math.random)}}
       [:i.twa.twa-cloud.twa-3x]])
    [:i.twa.twa-fire.twa-5x]]])

(defn component-bounce-tiles []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:span.about
        [:div.title "Bounce tiles"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L9-L82"} "css"]]
       [:div.card {:on-click (fn []
                               (swap! on not)
                               (js/setTimeout
                                 #(reset! on false)
                                 1200))}
        [:span {:style {:transform "scaleY(0.66)"}}
         (doall
           (for [x (range 3)
                 y (range 3)]
             [:div (move (- (* x 100) 100) (- (* y 100) 100) {:key [x y]})
              [:i.twa.twa-white-large-square.twa-5x
               {:class (when @on "juicy__bounce")
                :style {:animation-delay (str (int (* (js/Math.random) 150)) "ms")}}]]))]]])))

(defn component-coin-up []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:span.about
        [:div.title "Coin up"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L84-L124"} "css"]]
       [:div.card {:on-click (fn [] (swap! on not))}
        (when @on
          [:i.twa.twa-coin.twa-5x
           {:class "juicy__coinup"
            :on-animation-end #(reset! on false)}])]])))

(defn component-title-sweep-in []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:span.about
        [:div.title "Title sweep"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L534-L553"} "css"]]
       [:div.card {:on-click #(swap! on not)}
        (when @on
          [:h1 {:class "juicy__titlesweep"} "Game Title!"])]])))

(defn component-title-spin-in []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:span.about
        [:div.title "Title spin"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L555-L578"} "css"]]
       [:div.card {:on-click #(swap! on not)}
        (when @on
          [:h1 {:class "juicy__titlespin"} "Game Title!"])]])))

(defn component-attack []
  (let [on (r/atom false)
        p (r/atom [])]
    (fn []
      [:div
       [:span.about
        [:div.title "Attack"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L400-L456"} "css"]]
       [:div.card {:on-click (fn []
                               (swap! on not)
                               (js/setTimeout #(swap! p concat (make-particle-attributes 10)) 300)
                               (js/setTimeout #(swap! on not) 1500))}
        [:div (move 20 10)
         (for [i @p]
           [:div.juicy__particle
            (assoc i :on-animation-end #(swap! p (fn [ps] (remove (partial = i) ps))))
            [:i.twa.twa-collision.twa-5x]])]
        [:div (move -50 0) [:i.twa.twa-elf-dark-skin-tone.twa-5x {:class (when @on "juicy__attack-initiate")}]]
        [:div (move 50 0) [:i.twa.twa-ogre.twa-5x {:class (when @on "juicy__attack-receive")}]]]])))

(defn component-particles []
  (let [p (r/atom [])
        q 20]
    (fn []
      [:div
       [:span.about
        [:div.title "Particles" (when (not-empty @p) (str " (" (count @p) ")"))]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L342-L398"} "css"]]
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
       [:span.about
        [:div.title "Bubble up tiles"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L279-L317"} "css"]]
       [:div.card {:on-click #(swap! on not)}
        [:span {:style {:transform "scaleY(0.66)"}}
         (when @on
           (for [x (range 3)
                 y (range 2)]
             [:div (move (- (* x 100) 100) (- (* y 100) 50) {:key [x y]})
              [:i.twa.twa-white-large-square.twa-5x.juicy__bubbleup
               {:class (str "juicy__bubbleup-" (inc (js/Math.floor (* (js/Math.random) 4))))
                :style {:animation-delay (str (int (* (js/Math.random) 500)) "ms")}}]]))]]])))

(defn component-screenshake []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:span.about
        [:div.title "Screenshake"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L234-L277"} "css"]]
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
       [:span.about
        [:div.title "Dash"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L205-L232"} "css"]]
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
       [:span.about
        [:div.title "Shaker"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L129-L203"} "css"]]
       [:div.card {:on-click #(swap! v (fn [o] (mod (inc o) 5)))}
        (when (< @v 3)
          [:svg.shadow (move 0 43 {:width 100 :height 100})
           [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5}]])
        (case @v
          0 [:i.twa.twa-grinning-face.twa-5x]
          1 [:i.twa.twa-grinning-face-with-sweat.twa-5x {:class "juicy__shake__1"}]
          2 [:i.twa.twa-zany-face.twa-5x {:class "juicy__shake__2"}]
          3 [:i.twa.twa-grimacing-face.twa-5x {:class "juicy__shake__3"}]
          4 [:i.twa.twa-knocked-out-face.twa-5x {:class "juicy__shake__4"}])]])))

(defn component-bounce []
  (let [on (r/atom false)]
    (fn []
      [:div
       [:span.about
        [:div.title "Bounce"]
        [:a.link {:target "_blank"
                  :href "https://github.com/chr15m/juice-it/blob/master/public/juice.css#L9-L82"} "css"]]
       [:div.card
        {:on-click #(reset! on true)}
        [:svg.shadow (move 0 43 {:width 100 :height 100})
         [:ellipse {:cx 50 :cy 50 :ry 5 :rx 30 :fill "#888" :opacity 0.5
                    :class (when @on "juicy__bounce__shadow")}]]
        (if @on
          [:i.twa.twa-zany-face.twa-5x {:class "juicy__bounce"
                                        :on-animation-end #(reset! on false)}]     
          [:i.twa.twa-grinning-face.twa-5x])]])))

;*** main ***;

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
       [:h2 "Juice-it CSS " [:i.twa.twa-tropical-drink]]
       [:h4 "A collection of juicy game-feel animations in CSS."]
       [:p "Use these CSS animations to add game-like interactivity to your web apps. Or " [:strong "a game!"] " " [:i.twa.twa-grinning-face]]
       [component-particles]
       [component-bounce]
       [component-bubble]
       [component-smoke]
       [component-shake]
       [component-bounce-tiles]
       [component-title-sweep-in]
       [component-title-spin-in]
       [component-coin-up]
       [component-attack]
       [component-float-tiles]
       [component-dash]
       [component-screenshake]
       [:footer
        [:p "Made with " [:i.twa.twa-robot] " by " [:a {:href "https://mccormick.cx/"} "Chris McCormick"] " (" [:a {:href "https://twitter.com/mccrmx"} "@mccrmx"] ")"]
        [:p "Inspired by " [:a {:href "https://www.youtube.com/watch?v=Fy0aCDmgnxg"} "Juice It or Lose It"] "."]]])))

(defn main []
  (js/setTimeout
    #(rdom/render [component-main state]
                  (js/document.getElementById "app"))
    500))
