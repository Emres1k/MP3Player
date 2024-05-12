package application;

import java.io.File;

public class ListeYapisi {
    Node head = null;
    Node tail = null;
    Node calan_muzik=head;
    
    void ekle(File file) {
        Node eleman = new Node();
        
        eleman.muzik_dosyasi=file;
        eleman.muzik_url_string=file.toURI().toString();
        eleman.muzik_label=file.getName();
        eleman.begenildi_mi=false;
        if (head == null) {
        	head = eleman;
            tail = eleman;
            
            tail.next=head; 
            tail.prev=head;
            head.next=tail;
            head.prev=tail; 
            calan_muzik=head;
        } else {
        	tail.next = eleman;
            eleman.prev=tail;
            tail = eleman;

            tail.next=head; // en önemli kısım
            head.prev=tail;
            calan_muzik=head;
        }

    }
    void bir_sonraki() {
    	calan_muzik = calan_muzik.next;
    }
    void bir_onceki() {
    	calan_muzik = calan_muzik.prev;
    }
    void muzik_degisimi(String muzik_adi) {
    	System.out.println(muzik_adi);
    	muzik_adi+=".mp3";
    	for(Node temp = head; temp != null; temp = temp.next) {
            if(muzik_adi.equals(temp.muzik_label)) {
            	calan_muzik=temp;
            }
        }
    }
    String calan_muzik_URI() {
    	return calan_muzik.muzik_url_string;
    }
    String calan_muzik_label() {
    	return calan_muzik.muzik_label;
    }
    Node node_dondur() {
    	return calan_muzik;
    }
    void begenme_durumunu_degistir() {
    	if(calan_muzik.begenildi_mi==false) {
    		calan_muzik.begenildi_mi=true;
    	}
    	else if(calan_muzik.begenildi_mi==true) {
    		calan_muzik.begenildi_mi=false;
    	
    	}
    	else {
    
    	
    	}
    }
    boolean begenildi_mi() {
    	return calan_muzik.begenildi_mi;
    }

}