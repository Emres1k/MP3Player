module MP3 {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.media;
	requires javafx.graphics;
	
	opens application to javafx.graphics, javafx.fxml;
}
