(ns docks.demo
  (:require [clojure.java.io :as io]
            [docks.core :as docks]
            [jfxutils.core :refer [add-children! event-handler jfxnew printexp
                                   run-now run-later set-items! set-list!
                                   set-menus! stage]])
  (:import (javafx.application Application)
           (javafx.scene Scene)
           (javafx.scene.control Label Menu MenuBar MenuItem Tab
                                 TabPane TableColumn TableView TextArea
                                 TreeItem TreeView)
           (javafx.scene.image Image)
           (javafx.scene.layout BorderPane)
           (javafx.stage Stage))
  (:gen-class))

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


(defn -start []
  (let [tabs (TabPane.)
        html-editor (run-now (javafx.scene.web.HTMLEditor.))
        table-view (TableView.)
        dock-image (Image. (.toExternalForm (io/resource "org/dockfx/demo/docknode.png")))
        new-text-node (fn [num]
                        (docks/node (TextArea. (slurp "loremipsum.txt"))
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
    ;; Start small then resize to get equal portions
    (.setPrefSize edit-base 1 1)
    (.setPrefSize tn1 1 1)
    (.setPrefSize tn2 1 1)
    (.setPrefSize tv 1 1)
    (.setPrefSize center-base 1 1)
    (.setPrefSize root-dock-pane 1 1)
    
    (set-list! tabs :tabs [(Tab. "Tab1" html-editor) (Tab. "Tab2") (Tab. "Tab3")])
    (set-list! table-view :columns (map #(TableColumn. %) ["A" "B" "C"]))

    (let [st (stage (jfxnew BorderPane
                            :center root-dock-pane
                            :bottom (Label. "Bottom")
                            :top (jfxnew MenuBar
                                         :menus [(jfxnew Menu "File"
                                                         :items [(jfxnew MenuItem "New Tab"
                                                                         :on-action (docks/dock (new-text-node 0) edit-base :center))])
                                                 (jfxnew Menu "Edit")] )) )]
      (run-now (.setTitle st (name :DockFX))
               (try (.setHtmlText html-editor (slurp "readme.html"))
                    (catch java.io.IOException e
                      (.printStackTrace e)))
               (Application/setUserAgentStylesheet Application/STYLESHEET_MODENA)
               (docks/init-style))
      ;; Resize down then up to get equal proportions
      (run-now (.setHeight st 400))
      (run-now (.setWidth st 400))
      ))
  




  (defn main []
    (jfxutils.core/app-init)
    (-start)))

(defn -main []
  (jfxutils.core/app-init)
  (-start))



