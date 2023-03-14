module nl.maastrichtuniversity.dke.gamecontrollersample {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires org.testng;
    requires com.google.gson;

    exports nl.maastrichtuniversity.dke.gamecontrollersample.gameplay;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.gameplay to javafx.fxml, com.google.gson;
    exports nl.maastrichtuniversity.dke.gamecontrollersample.agents;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.agents to javafx.fxml;
    exports nl.maastrichtuniversity.dke.gamecontrollersample.tools;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.tools to javafx.fxml;
    exports nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures to javafx.fxml;
    exports nl.maastrichtuniversity.dke.gamecontrollersample.tools.AStar;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.tools.AStar to javafx.fxml;
    exports nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers to javafx.fxml;
    exports nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Exceptions;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Exceptions to javafx.fxml;
    exports nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.agentsfeatures.sound to javafx.fxml;
    opens nl.maastrichtuniversity.dke.gamecontrollersample.experiments to com.google.gson;
}