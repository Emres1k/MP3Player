package application;


public class Begendiklerim_Listesi {
    Begendiklerim_Node head = null;
    Begendiklerim_Node tail;

    public Begendiklerim_Listesi() {
    }

    
    void ekle(String muzik_ismi) {
    	Begendiklerim_Node eleman = new Begendiklerim_Node(muzik_ismi);
        eleman.muzik_label = muzik_ismi;
        if (head == null) {
            eleman.next = null;
            head = eleman;
            tail = eleman;
        } else {
            eleman.next = null;
            tail.next = eleman;
            tail = eleman;
        }
    }
    void sil(String muzik_ismi) {
        // Liste boş ise işlem yapma
        if (head == null) {
            return;
        }

        // Eğer listedeki ilk elemanı silmemiz gerekiyorsa
        if (head.muzik_label.equals(muzik_ismi)) {
            head = head.next;
            // Eğer liste sadece bir elemandan oluşuyorsa tail'i de güncelle
            if (head == null) {
                tail = null;
            }
            return;
        }

        // Listenin diğer elemanlarını kontrol et
        Begendiklerim_Node current = head;
        while (current.next != null) {
            if (current.next.muzik_label.equals(muzik_ismi)) {
                // Silinecek elemanı bağlantıdan çıkar
                current.next = current.next.next;
                // Eğer silinen eleman listenin son elemanı ise tail'i güncelle
                if (current.next == null) {
                    tail = current;
                }
                return;
            }
            current = current.next;
        }
    }
    
    Begendiklerim_Node head_dondur() {
    	return head;
    }
}
