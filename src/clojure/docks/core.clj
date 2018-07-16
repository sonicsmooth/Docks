(ns docks.core
  (:import (javafx.scene.image ImageView))
  (:gen-class))


(def ^{:private true} DOCKFX-POS-MAP 
  {:center org.dockfx.DockPos/CENTER
   :top org.dockfx.DockPos/TOP 
   :bottom org.dockfx.DockPos/BOTTOM
   :left org.dockfx.DockPos/LEFT
   :right org.dockfx.DockPos/RIGHT})


(defn- pos [p]
  (DOCKFX-POS-MAP p))

(defn node
  "Creates dock node in the current docking system.  contents is a
  javafx node.  title is a String.  icon is a javafx.scene.image.Image"
  ([contents]
   (org.dockfx.DockNode. contents))
  ([contents title]     ;; works with nil title!
   (org.dockfx.DockNode. contents title))
  ([contents title icon]
   (org.dockfx.DockNode. contents title (ImageView. icon)))) ;; ensure non-draggable

(defn dock
  "Attach node n to base b at position poz with optional percentage
  perc."
  [n b poz & [perc]]
  (.dock n b (pos poz)))

(defn base
  "Creates a base aka docking station aka dock-pane in the current
  docking system.  One or more docking nodes can be supplied for each
  key position, and each key position can be specified more than once  "
  ([]
   (org.dockfx.DockPane.))
  ([ & args]
   ;; Assume if no args are passed, then zero-arity fn is called
   (let [b (base)
         ensure-seq (fn [item]  (if (sequential? item) item (vector item)))]
     (doseq [[poz content] (partition 2 (concat args))] 
       (let [dnode-vec (ensure-seq content)] ;; content could be single dock-node or list, so make it a list
         (doseq [dnode dnode-vec]
           (dock dnode b poz))))
     (.setExclusive b true) ;; default exclusive
     b)))




(defn init-style []
  (org.dockfx.DockPane/initializeDefaultUserAgentStylesheet))



















