(ns docks.core
  (:gen-class)
  (:use [jfxutils.core])
  (:import [javafx.application Application]
           [javafx.scene.image Image ImageView]))


(def DOCK-SYSTEM :DockFX)

(defn- verify-system
  "Check whether DOCK-SYSTEM is bound and one of the two allowed
  systems"
  []
  (when (not (and (bound? #'DOCK-SYSTEM)
                     (#{:DockFX :AnchorFX} DOCK-SYSTEM)))
       (throw (Exception. "Must specify docking system with (set-docking-system! ...)")))
  true)

(def ^{:private true} DOCKFX-POS-MAP 
  {:center org.dockfx.DockPos/CENTER
   :top org.dockfx.DockPos/TOP 
   :bottom org.dockfx.DockPos/BOTTOM
   :left org.dockfx.DockPos/LEFT
   :right org.dockfx.DockPos/RIGHT})

(def ^{:private true} ANCHORFX-POS-MAP 
  {:center com.anchorage.docks.node.DockNode$DockPosition/CENTER
   :top com.anchorage.docks.node.DockNode$DockPosition/TOP
   :bottom com.anchorage.docks.node.DockNode$DockPosition/BOTTOM
   :left com.anchorage.docks.node.DockNode$DockPosition/LEFT
   :right com.anchorage.docks.node.DockNode$DockPosition/RIGHT})

(defn- pos [p]
  {:pre [(verify-system)]}
  (case DOCK-SYSTEM
    :DockFX (DOCKFX-POS-MAP p)
    :AnchorFX (ANCHORFX-POS-MAP p)))

(defn set-docking-system!
  "Set the global var to one of two allowed values"
  [system]
  {:pre [(#{:DockFX :AnchorFX} system)]}
  (def DOCK-SYSTEM system))

(defn node
  "Creates dock node in the current docking system.  contents is a
  javafx node.  title is a String.  icon is a javafx.scene.image.Image"
  ([contents]
   {:pre [(verify-system)]}
   (case DOCK-SYSTEM
     :DockFX (org.dockfx.DockNode. contents)
     :AnchorFX (com.anchorage.system.AnchorageSystem/createDock "" contents)))
  ([contents title]
   {:pre [(verify-system)]}
   (case DOCK-SYSTEM
     :DockFX (org.dockfx.DockNode. contents title) ;; works with nil title!
     :AnchorFX (com.anchorage.system.AnchorageSystem/createDock title contents)))
  ([contents title icon]
   {:pre [(verify-system)]}
   (case DOCK-SYSTEM
     :DockFX (let [n (org.dockfx.DockNode. contents title (ImageView. icon))]
               ;;(when (nil? title) (.setDockTitleBar n nil)) ;; no longer needed
               n);; ensure non-draggable
     :AnchorFX (com.anchorage.system.AnchorageSystem/createDock title contents icon))))

(defn dock
  "Attach node n to base b at position poz with optional percentage
  perc.  When using AnchorFX, b can be a BaseStation, an SubStation,
  or another DockNode."
  [n b poz & [perc]]
  (.dock n b (pos poz)))

(defn base
  "Creates a base aka docking station aka dock-pane in the current
  docking system.  One or more docking nodes can be supplied for each
  key position, and each key position can be specified more than once  "
  ([]
   {:pre [(verify-system)]}
   (cond (= DOCK-SYSTEM :DockFX) (org.dockfx.DockPane.)
         (= DOCK-SYSTEM :AnchorFX)   (com.anchorage.system.AnchorageSystem/createStation)))
  ([ & args]
   ;; Assume if no args are passed, then zero-arity fn is called
   (let [b (base)
         ensure-seq (fn [item]  (if (sequential? item) item (vector item)))]
     (doseq [[poz content] (partition 2 (concat args))] 
       (let [dnode-vec (ensure-seq content)] ;; content could be single dock-node or list, so make it a list
         (doseq [dnode dnode-vec]
           (dock dnode b poz))))
     (when (= DOCK-SYSTEM :DockFX) (.setExclusive b true)) ;; default exclusive
     b)))





(defn init-style []
  {:pre [(verify-system)]}
  ;;(Application/setUserAgentStylesheet Application/STYLESHEET_MODENA)
  (case DOCK-SYSTEM
    :DockFX (org.dockfx.DockPane/initializeDefaultUserAgentStylesheet)
    :AnchorFX (com.anchorage.system.AnchorageSystem/installDefaultStyle)))



















