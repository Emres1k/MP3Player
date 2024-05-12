package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Scene.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Image ikon = new Image("9973495.png");
		stage.getIcons().add(ikon);
		stage.show();
		stage.setResizable(false);
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				
				Platform.exit();
				System.exit(0);	
			}		
		});
	}	

	public static void main(String[] args) {
		launch(args);
	}
}