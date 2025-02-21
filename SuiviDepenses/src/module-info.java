module SuiviDepenses {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires javafx.base;
	

    opens Depenses to javafx.base, javafx.graphics;
    opens Graphique to javafx.graphics, javafx.fxml;
    opens Basedonnes to javafx.base;
}