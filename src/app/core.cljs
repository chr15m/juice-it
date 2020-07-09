(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defonce state (r/atom {}))

(defn component-main [state]
  [:div "Hello world."])

(defn main []
  (print "hi"))

(rdom/render [component-main state]
             (js/document.getElementById "app"))
