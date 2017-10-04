(defproject docks "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.dockfx/DockFX "0.1.12"]
                 [com.anchorfx/anchorfx "0.1-SNAPSHOT"]
                 [jfxutils "0.1.0-SNAPSHOT"]]

  :resource-paths ["resources"
                   "resources/anchor_resources"
                   "resources/dock_resources"] 
  :main docks.demo
  :aot :all
  )

