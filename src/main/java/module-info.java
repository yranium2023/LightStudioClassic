module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.vproxy.vfx;
    requires io.vproxy.base;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.media;
    requires java.logging;
    requires java.desktop;
    requires com.github.kwhat.jnativehook;
    requires vjson;


    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.Curve.SplineCanvas;
    exports org.example.ImageModification;
    exports org.example.Obj;
}