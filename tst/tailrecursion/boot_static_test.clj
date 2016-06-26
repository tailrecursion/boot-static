(ns tailrecursion.boot-static-test
  (:require
    [clj-http.client   :as http]
    [ring.mock.request :as mock]
    [clojure.test :refer :all]))

(def uri "http://localhost:3006/")

;;; tests ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest test-get
  (let [res (http/get (str uri "nonexistent") {:throw-exceptions false})]
    (is (= (res :status) 404))))
