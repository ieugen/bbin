(ns babashka.bbin.util
  (:require [babashka.fs :as fs]
            [babashka.process :as p]
            [babashka.bbin.meta :as meta]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [taoensso.timbre :as log]))

(defn user-home []
  (System/getProperty "user.home"))

(defn sh [cmd & {:as opts}]
  (doto (p/sh cmd (merge {:err :inherit} opts))
    p/check))

(defn set-logging-config! [{:keys [debug]}]
  (log/merge-config! {:min-level (if debug :debug :warn)}))

(defn pprint [x _]
  (pprint/pprint x))

(defn print-help [& _]
  (println (str/trim "
Usage: bbin <command>

  bbin install    Install a script
  bbin uninstall  Remove a script
  bbin ls         List installed scripts
  bbin bin        Display bbin bin folder
  bbin version    Display bbin version
  bbin help       Display bbin help")))

(def ^:dynamic *bin-dir* nil)

(defn print-legacy-path-warning []
  (binding [*out* *err*]
    (println (str/triml "
WARNING: The $HOME/.babashka/bbin/bin path is deprecated in favor of ~/.local/bin.
WARNING:
WARNING: To remove this message, you can either:
WARNING:
WARNING: Migrate:
WARNING:   - Move ~/.babashka/bbin/bin to ~/.local/bin
WARNING:   - Move ~/.babashka/bbin/jars to ~/.cache/babashka/bbin/jars (if it exists)
WARNING:
WARNING: OR
WARNING:
WARNING: Override:
WARNING:   - Set the BABASHKA_BBIN_BIN_DIR env variable to \"$HOME/.babashka/bbin\"
"))))

(defn- using-legacy-paths? []
  (fs/exists? (fs/file (user-home) ".babashka" "bbin" "bin")))

(defn check-legacy-paths []
  (when (using-legacy-paths?)
    (print-legacy-path-warning)))

(defn bin-dir-base [_]
  (if-let [override (System/getenv "BABASHKA_BBIN_BIN_DIR")]
    (fs/file override)
    (if (using-legacy-paths?)
      (fs/file (user-home) ".babashka" "bbin" "bin")
      (fs/file (user-home) ".local" "bin"))))

(defn bin-dir [opts]
  (or *bin-dir* (bin-dir-base opts)))

(defn- xdg-cache-home []
  (if-let [override (System/getenv "XDG_CACHE_HOME")]
    (fs/file override)
    (fs/file (user-home) ".cache")))

(def ^:dynamic *jars-dir* nil)

(defn jars-dir-base [_]
  (if-let [override (System/getenv "BABASHKA_BBIN_JARS_DIR")]
    (fs/file override)
    (if (using-legacy-paths?)
      (fs/file (user-home) ".babashka" "bbin" "jars")
      (fs/file (xdg-cache-home) "babashka" "bbin" "jars"))))

(defn jars-dir [opts]
  (or *jars-dir* (jars-dir-base opts)))

(defn canonicalized-cli-opts [cli-opts]
  (merge cli-opts
         (when-let [v (:local/root cli-opts)]
           {:local/root (str (fs/canonicalize v {:nofollow-links true}))})))

(defn ensure-bbin-dirs [cli-opts]
  (fs/create-dirs (bin-dir cli-opts)))

(def windows?
  (some-> (System/getProperty "os.name")
    (str/lower-case)
    (str/index-of "win")))

(defn print-version [& {:as opts}]
  (if (:help opts)
    (print-help)
    (println "bbin" meta/version)))

(defn- parse-version [version]
  (mapv #(Integer/parseInt %)
        (-> version
            (str/replace "-SNAPSHOT" "")
            (str/split #"\."))))

(defn- satisfies-min-version? [current-version min-version]
  (let [[major-current minor-current patch-current] (parse-version current-version)
        [major-min minor-min patch-min] (parse-version min-version)]
    (or (> major-current major-min)
        (and (= major-current major-min)
             (or (> minor-current minor-min)
                 (and (= minor-current minor-min)
                      (>= patch-current patch-min)))))))

(defn check-min-bb-version []
  (let [current-bb-version (System/getProperty "babashka.version")]
    (when (and meta/min-bb-version (not= meta/min-bb-version :version-not-set))
      (when-not (satisfies-min-version? current-bb-version meta/min-bb-version)
        (binding [*out* *err*]
          (println (str "WARNING: this project requires babashka "
                        meta/min-bb-version " or newer, but you have: "
                        current-bb-version)))))))

(defn snake-case [s]
  (str/replace s "_" "-"))
