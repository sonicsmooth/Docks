(ns docks.demo
  (:gen-class)
  (:use [jfxutils.core :exclude [-main]])
  (:require [docks.core :as docks]
            [clojure.java.io :as io])
  (:import [java.io.IOException]
           [java.nio.file Files Paths]
           [javafx.application Application]
           [javafx.scene Scene]
           [javafx.scene.control
            Tab TabPane TableColumn TableView TreeItem TreeView Label TextArea
            MenuBar Menu MenuItem]
           [javafx.scene.image Image ImageView]
           [javafx.scene.layout BorderPane]
           [javafx.scene.web.HTMLEditor]
           [javafx.stage Stage]))

(defn generate-random-tree []
  (let [root (TreeItem.)
        tree-view (TreeView. root)]
    (.setShowRoot tree-view false)
    (doseq [i (range (+ 4 (rand-int 8)))]
      (let [tree-item (TreeItem. (str "Item " i))]
        (add-children! root [tree-item])
        (doseq [j (range (+ 2 (rand-int 4)))]
          (add-children! tree-item [(TreeItem. (str "Child " j)) ]))))
    tree-view))


(defn -start [primary-stage]
  (let [ds :DockFX]
    (.setTitle primary-stage (name ds))
    (docks/set-docking-system! ds))

  (let [tabs (TabPane.)
        html-editor (javafx.scene.web.HTMLEditor.)
        table-view (TableView.)
        dock-image (Image. (.toExternalForm (io/resource "docknode.png")))
        new-text-node (fn [num] (docks/node (TextArea. (slurp "loremipsum.txt"))
                                            (str "Text " num)))
        edit-base (docks/base :center (map new-text-node (range 3)))
        tn1 (docks/node (generate-random-tree) "Tree Node1" dock-image)
        tn2 (docks/node (generate-random-tree) "Tree Node2" dock-image)
        tv (docks/node table-view "Table node")
        center-base (docks/node edit-base)
        root-dock-pane (docks/base :center center-base
                                   :left tn2
                                   :right tn1
                                   :bottom tv)]
    (.setPrefSize center-base 300 600)
    ;;(.setPrefSize tn1 10 60)
    ;;(.setPrefSize tn2 10 60)
    (.setPrefSize tv 300 100)


    (set-list! tabs :tabs [(Tab. "Tab1" html-editor) (Tab. "Tab2") (Tab. "Tab3")])
    (set-list! table-view :columns (map #(TableColumn. %) ["A" "B" "C"]))

    (try (.setHtmlText html-editor (slurp "readme.html"))
         (catch java.io.IOException e
           (.printStackTrace e)))
    
    (.setScene primary-stage
               (Scene.
                (jfxnew BorderPane
                        :center root-dock-pane
                        :bottom (Label. "Bottom")
                        :top (jfxnew MenuBar :menus
                                     [(jfxnew Menu "File" :items
                                              [(jfxnew MenuItem "New Tab"
                                                       :on-action (event-handler [e] (docks/dock (new-text-node 0) edit-base :center)))])
                                      (jfxnew Menu "Edit")] ))
                ;;1280 600
                ))

    (.show primary-stage)
    (Application/setUserAgentStylesheet Application/STYLESHEET_MODENA)
    (docks/init-style)))




(defn main []
  (jfxutils.core/app-init)
  (run-now (-start (Stage.))))

(defn -main []
  (jfxutils.core/app-init)
  (run-now (-start (Stage.))))



