(ns pncgame.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)


(defonce grid-config (atom {:top 103
                            :left 0
                            :totalspr 4
                            :width 72
                            :height 94}))

(def anim-controler (atom {:curr-frame 0
                           :mirror ""
                           :x (* -1 (:left @grid-config))
                           :y (* -1 (:top @grid-config))}))


(defonce animateid (atom nil))

(defn next-frame []
  (if (>= (inc (:curr-frame @anim-controler)) (:totalspr @grid-config))
    (swap! anim-controler assoc :curr-frame 0)
    (swap! anim-controler update-in [:curr-frame] inc))
  
  (let [{:keys [curr-frame x y]} @anim-controler
        {:keys [top left width height]} @grid-config]
    (letfn [(update-attr [x] (* -1 (+ (* curr-frame x) 3 left)))]
      (swap! anim-controler assoc
             :x (update-attr width)))))

(defn reset-frame []
  (swap! anim-controler assoc :curr-frame 0)
  (next-frame))

(defn animate []
  (let [id (if (nil? @animateid)
             (js/setInterval #(next-frame) 250)
             (do
               (reset-frame)
               (js/clearInterval @animateid)
               nil))]
    (reset! animateid id)))

(defn animate-preview []
  [:div {:margin-top "30px"}
   [:input {:type "button"
            :value (if (nil? @animateid) "Start" "Stop")
            :on-click #(animate)}]
   
   
   [:div {:style {:margin-top "10px"
                  :border-size "1px"
                  :border-width "thin"
                  :border-style "solid"
                  :border-color "#000"
                  :transform (:mirror @anim-controler)
                  :width (:width @grid-config)
                  :height (:height @grid-config)
                  :background-image "url(imgs/sprite1.png)"
                  :background-repeat "none"
                  :background-position (str (:x @anim-controler) "px "
                                            (:y @anim-controler) "px")}}]])

(defn grid-controler []
  [:div.controler
   [:div
    [:label "Sprites in row:"]
    [:input {:type "text"
             :size 3
             :value (:totalspr @grid-config)
             :on-change #(swap! grid-config assoc :totalspr (-> % .-target .-value int))}]]
   [:div
    [:label "Width: "]
    [:input {:type "text"
             :size 3
             :value (:width @grid-config)
             :on-change #(swap! grid-config assoc :width (-> % .-target .-value int))}]]
   [:div
    [:label "Height: "]
    [:input {:type "text"
             :size 3
             :value (:height @grid-config)
             :on-change #(swap! grid-config assoc :height (-> % .-target .-value int))}]]
   
   [:div
    [:label "Top: "]
    [:input {:type "text"
             :size 3
             :value (:top @grid-config)
             :on-change #(do
                           (swap! grid-config assoc :top (-> % .-target .-value int))
                           (swap! animate-preview assoc :y (-> % .-target .-value int)))}]]
   [:div
    [:label "Left: "]
    [:input {:type "text"
             :size 3
             :value (:left @grid-config)
             :on-change #(do
                           (swap! grid-config assoc :left (-> % .-target .-value int))
                           (swap! animate-preview assoc :x (-> % .-target .-value int)))}]]

   (animate-preview)])

(defn grid []
  [:div {:key (str "grid-" (rand-int 1024))
         :style {:position "relative"
                 :float "left"
                 :width (:width @grid-config)
                 :height (:height @grid-config)
                 :border-size 1
                 :border-width "thin"
                 :border-style "solid"
                 :border-color "#000"}}])

(defn sprite-image []
  [:div
   [:img {:src "imgs/sprite1.png"}]])

(defn grids []
  [:div.grids
   (sprite-image)
   [:div {:style {:position "absolute"
                  :top (:top @grid-config)
                  :left (:left @grid-config)}}
    (doall (repeatedly (:totalspr @grid-config) #(grid)))]])



(defn hello-world []
  [:div
   (grid-controler)
   (grids)])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))


(defn on-js-reload [])
