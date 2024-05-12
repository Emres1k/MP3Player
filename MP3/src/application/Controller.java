package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Popup;
import javafx.util.Duration;

public class Controller implements Initializable{
	
	
	@FXML
	private Pane pane;
	@FXML
	private Label muzik_label, sanatci_label, caliniyor_label,toplam_sure_label,kalan_sure_label;
	@FXML
	private Button playButton, pauseButton, previousButton, nextButton,begendiklerim_buton, begenButton;
	@FXML
	private ComboBox<String> speedBox;
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar songProgressBar;
	@FXML
	private ImageView kapak_resmi;
	@FXML
	private ImageView button_image;
	@FXML
	private ImageView begenme_image;
	
	@FXML
	private ImageView ses_image;
	@FXML
    private ListView<String> begendiklerim_ListView;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	
	private File directory;
	private File[] files;
	
	ListeYapisi muzikler = new ListeYapisi();
	Begendiklerim_Listesi begendigim_muzikler=new Begendiklerim_Listesi();

	
	private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
	
	private Timer timer;
	private TimerTask task;
	
	private boolean running;
	
	private double yuzde_kac_tiklandi;
	private boolean ilerleme_cubug_tiklandi_mi=false;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		muzikleri_klasorden_cek_ve_ilk_muzigi_baslat();
		hiz_secenekleri_ekle();
		ses_seviyesi_ekle();
		muzik_begenme_icon_kontrol_et();
		
		songProgressBar.setStyle("-fx-accent: green;"); // Yeşil renk
	
	}
	public void muzigi_baslat_durdur() {
		if(running) {
			zamanlayiciyi_durdur();
			hizi_degistir(null);
			mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
			mediaPlayer.pause();
			File file = new File("iconlar/play.png");
			Image play_png = new Image(file.toURI().toString());
			button_image.setImage(play_png);
			
		}
		else {
			zamanlayiciyi_baslat();
			mediaPlayer.play();
			File file = new File("iconlar/pause.png");
			Image pause_png = new Image(file.toURI().toString());
	        button_image.setImage(pause_png);
		}		
	}
	public void muzigi_sifirla() {
		
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0));
	}
	public void onceki_muzik() {
		
		mediaPlayer.stop();
		
		if(running) {
			
			zamanlayiciyi_durdur();
		}
		
		muzikler.bir_onceki();
		media = new Media(muzikler.calan_muzik_URI()); 
		mediaPlayer = new MediaPlayer(media);
		
		kapak_resmini_degistir();
		labellari_degistir();
		muzigi_baslat_durdur();
		muzik_begenme_icon_kontrol_et();
	}
	public void sonraki_muzik() {
		
		mediaPlayer.stop();
		
		if(running) {
			
			zamanlayiciyi_durdur();
		}
		
		muzikler.bir_sonraki();
		media = new Media(muzikler.calan_muzik_URI());  
		mediaPlayer = new MediaPlayer(media);
		
		kapak_resmini_degistir();
		labellari_degistir();
		muzigi_baslat_durdur();
		muzik_begenme_icon_kontrol_et();
	}
	public void hizi_degistir(ActionEvent event) {
		
		if(speedBox.getValue() == null) {
			
			mediaPlayer.setRate(1);
		}
		else {
			
			//mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
			mediaPlayer.setRate(Integer.parseInt(  // stringi inte dönüştürür
					speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
	//										başlangıçtan başla		yüzde ifadesini siliyoruz burda
												
		}
	}
	public void zamanlayiciyi_baslat() {
		
		timer = new Timer();
		task = new TimerTask() {
			
			public void run() {
				double current;
				double end;
				running = true;
				end = media.getDuration().toSeconds();

				if(ilerleme_cubug_tiklandi_mi) {
					current=(yuzde_kac_tiklandi)*end;
					mediaPlayer.seek(Duration.seconds(current));
					ilerleme_cubug_tiklandi_mi=false;
				}
				else 
					current = mediaPlayer.getCurrentTime().toSeconds();
				
			
				songProgressBar.setProgress(current/end);
				int suanki_dakika = (int) current / 60;
				int suanki_saniye = (int) current % 60;
				int toplam_dakika = (int) end / 60;
				int toplam_saniye = (int) end % 60;
				
				Platform.runLater(() -> {
					if(current/end == 1) 
						sonraki_muzik();// müzik bittikten sonra yeniden çalma kısmı
					labellari_degistir();
					kalan_sure_ve_toplam_sure_label_ayarla(suanki_dakika,suanki_saniye,toplam_dakika,toplam_saniye);
				});
			}
		};
		
		timer.scheduleAtFixedRate(task, 0, 50);
		// timer.scheduleAtFixedRate(task, delay, period) delay dediği ilk başalığı zaman
	}	
	public void zamanlayiciyi_durdur() {
		
		running = false;
		timer.cancel();
	}
	public void muzikleri_klasorden_cek_ve_ilk_muzigi_baslat() {
		directory = new File("music");
		files = directory.listFiles();
		
		if(files != null) {
			
			for (int i = 0; i < files.length; i++) {
			    File file = files[i];
			    muzikler.ekle(file);
			}
		}
		
		media = new Media(muzikler.calan_muzik_URI());  // ilk eleman olduğu için
		mediaPlayer = new MediaPlayer(media);
		
		int uzunluk = muzikler.calan_muzik_label().length();
		muzik_label.setText(muzikler.calan_muzik_label().substring(0, uzunluk - 4));
		
		muzigi_baslat_durdur();
	}
	public void hiz_secenekleri_ekle() {
		for(int i = 0; i < speeds.length; i++) {
			
			speedBox.getItems().add(Integer.toString(speeds[i])+"%");
		}
		
		speedBox.setOnAction(this::hizi_degistir);
	}
	public void ses_seviyesi_ekle() {
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() { // ses seviyesi

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);			
			}
		});
	}
	@FXML
	public void ilerleme_cubugu_tiklama(MouseEvent event) {
		yuzde_kac_tiklandi = event.getX() / songProgressBar.getWidth();
	    songProgressBar.setProgress(yuzde_kac_tiklandi);
	    ilerleme_cubug_tiklandi_mi=true;
	}
	public void labellari_degistir() {
		int uzunluk = muzikler.calan_muzik_label().length();
		muzik_label.setText(muzikler.calan_muzik_label().substring(0, uzunluk - 4));
		
		int boslukIndex = muzikler.calan_muzik_label().indexOf("-");
		if (sanatci_label != null) {
	        sanatci_label.setText(muzikler.calan_muzik_label().substring(0,boslukIndex));
	    } else {
	    	sanatci_label.setText(null);
	    }
		if (caliniyor_label != null) {
			caliniyor_label.setText("Sanatçıdan Çalınıyor\n" + muzikler.calan_muzik_label().substring(0,boslukIndex));
	    } else {
	        caliniyor_label.setText(null);
	    }
	}
	public void kapak_resmini_degistir() {
		int uzunluk = muzikler.calan_muzik_label().length();
		muzik_label.setText(muzikler.calan_muzik_label().substring(0, uzunluk - 4));
		
        File directory = new File("kapaklar/"); 

        if (directory.exists() && directory.isDirectory()) {
            File[] resim_yollari = directory.listFiles();

            if (resim_yollari != null) {
                for (File resim_yolu : resim_yollari) {
                	if (muzikler.calan_muzik_label().substring(0, uzunluk - 4)
                			.equals(resim_yolu.getName().substring(0, resim_yolu.getName().length() - 4))) {
                        
                        Image resim = new Image(resim_yolu.toURI().toString());             
                        kapak_resmi.setImage(resim);
                        break; 
                    }
                }
            }
        } else {
            System.out.println("Belirtilen dizin bulunamadı veya bir dizin değil.");
        }
	}
	public void kalan_sure_ve_toplam_sure_label_ayarla(
			int suanki_dakika,int suanki_saniye,int toplam_dakika, int toplam_saniye) {
		
		kalan_sure_label.setText(suanki_dakika + ":" + suanki_saniye);
		toplam_sure_label.setText(toplam_dakika + ":" + toplam_saniye);
	
	}
	@FXML
	public void muzik_begenme_durumunu_kontrol_et() {
	    int uzunluk = muzikler.calan_muzik_label().length();
	    
	    // Müzik daha önce beğenilmiş mi kontrol et
	    if (muzikler.begenildi_mi()) {
	        // Eğer beğenilmişse, beğenme durumunu değiştir ve beğenilen müzikler listesinden çıkar
	        muzikler.begenme_durumunu_degistir();
	        begendigim_muzikler.sil((muzikler.calan_muzik_label().substring(0, uzunluk - 4)));
	       
	    } else {
	        // Eğer beğenilmemişse, beğenme durumunu değiştir ve beğenilen müzikler listesine ekle
	        muzikler.begenme_durumunu_degistir();
	        begendigim_muzikler.ekle((muzikler.calan_muzik_label().substring(0, uzunluk - 4)));
	       
	    }
	    muzik_begenme_icon_kontrol_et();
	}
	public void muzik_begenme_icon_kontrol_et() {
	  
	    if (muzikler.begenildi_mi()) {
	       
	        File file = new File("iconlar/doluLike.png");
		    Image begenildi_png = new Image(file.toURI().toString());
		    begenme_image.setImage(begenildi_png);
	    } 
	    else {
	
	        File file = new File("iconlar/Like.png");
		    Image begenilmedi_png = new Image(file.toURI().toString());
		    begenme_image.setImage(begenilmedi_png);
	    }
	}
	public void begendiklerim_listesini_ayarla() {
		ArrayList<String> stringList = new ArrayList<>();;
		Begendiklerim_Node temp_node=begendigim_muzikler.head_dondur();
		
        while(temp_node!=null){
            	stringList.add(temp_node.muzik_label);
            	temp_node=temp_node.next;
        }
        list_view_icindeki_muzikleri_sirala(stringList);
        if(stringList!=null) {
        	ObservableList<String> observableArrayList = FXCollections.observableArrayList(stringList);
            begendiklerim_ListView.setItems(observableArrayList);
        };
	}
	
	public void popup_goster() {
	    begendiklerim_ListView.setVisible(true);
	    begendiklerim_listesini_ayarla();
	    
	    Popup popup = new Popup();
	    popup.getContent().addAll(begendiklerim_ListView);
	    popup.setAutoHide(true); // aşağıdakş fonksiyona git

	    // Popup'ı göster
	    popup.show(begendiklerim_buton.getScene().getWindow());
	    
	    // Popup gizlendiğinde içeriği temizle ve ListView'ı gizle
	    popup.setOnHidden(e -> {
	        popup.getContent().clear();
	        begendiklerim_ListView.setVisible(false);
	    }); 
	}
	@FXML
	public void sesImageTiklandi() {
	    if (mediaPlayer != null) {
	        if (mediaPlayer.getVolume() == 0.0) {
	            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01); // Ses seviyesini önceki değere geri yükle
	            File file = new File("iconlar/Volume.png");
			    Image ses_png = new Image(file.toURI().toString());
			    ses_image.setImage(ses_png);
	        } else {
	            mediaPlayer.setVolume(0.0); // Ses seviyesini sıfıra indir
	            File file = new File("iconlar/kisikVolume.png");
			    Image kisik_ses_png = new Image(file.toURI().toString());
			    ses_image.setImage(kisik_ses_png);
	        }
	    }
	}
	public void list_view_icindeki_muzikleri_sirala(ArrayList<String> begendigim_muzikler) {
        int n = begendigim_muzikler.size();
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (begendigim_muzikler.get(j).compareTo(begendigim_muzikler.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) { 
                String temp = begendigim_muzikler.get(i);
                begendigim_muzikler.set(i, begendigim_muzikler.get(minIndex));
                begendigim_muzikler.set(minIndex, temp);
            }
        }
    }
}