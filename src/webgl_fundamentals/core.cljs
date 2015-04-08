(ns webgl-fundamentals.core)

(defn create-program
  [gl shaders]n
  (let [program (.createProgram gl)]
    (doseq [shader shaders]
      (.attachShader gl program shader))
    (doto gl
      (.linkProgram program)
      (.useProgram program))
    program))

(defn load-shader!
  [gl shader-source shader-type]
  (let [shader (.createShader gl shader-type)]
    (doto gl
      (.shaderSource shader shader-source)
      (.compileShader shader))
    (if (.getShaderParameter gl shader gl/COMPILE_STATUS)
      shader
      (.log js/console (.getShaderInfoLog gl shader)))))

(defn create-shader-from-script
  [gl script-id]
  (let [shader-script (.getElementById js/document script-id)
        shader-source (.-text shader-script)
        shader-type   (case (.-type shader-script)
                        "x-shader/x-vertex"   gl/VERTEX_SHADER
                        "x-shader/x-fragment" gl/FRAGMENT_SHADER)]
    (load-shader! gl shader-source shader-type)))

(defn create-program-from-scripts
  [gl & script-ids]
  (let [shaders (map (partial create-shader-from-script gl) script-ids)]
    (create-program gl shaders)))

(defn make-rect
  [x y width height]
  (let [x1 x
        x2 (+ x width)
        y1 y
        y2 (+ y height)]
    (js/Float32Array. #js [x1 y1
                           x2 y1
                           x1 y2
                           x1 y2
                           x2 y1
                           x2 y2])))

(defn random-int
  [range]
  (->> (.random js/Math)
       (* range)
       (.floor js/Math)))

(defn ^:export main []
  (let [canvas              (.getElementById js/document "canvas")
        gl                  (.getContext canvas "experimental-webgl")
        program             (create-program-from-scripts
                             gl "2d-vertex-shader" "2d-fragment-shader")
        position-location   (.getAttribLocation gl program "a_position")
        resolution-location (.getUniformLocation gl program "u_resolution")
        color-location      (.getUniformLocation gl program "u_color")
        buffer              (.createBuffer gl)]
    (doto gl
      (.bindBuffer gl/ARRAY_BUFFER buffer)
      (.enableVertexAttribArray position-location)
      (.vertexAttribPointer position-location 2 gl/FLOAT false 0 0)
      (.uniform2f resolution-location (.-width canvas) (.-height canvas)))
    (dotimes [n 50]
      (let [rect (make-rect (random-int 300) (random-int 300)
                            (random-int 300) (random-int 300))]
        (doto gl
          (.uniform4f color-location
                      (.random js/Math) (.random js/Math) (.random js/Math) 1)
          (.bufferData gl/ARRAY_BUFFER rect gl/STATIC_DRAW)
          (.drawArrays gl/TRIANGLES 0 6))))))

(main)
