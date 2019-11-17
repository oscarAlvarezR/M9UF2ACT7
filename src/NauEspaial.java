import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class NauEspaial extends javax.swing.JFrame {    

	public NauEspaial() {
		initComponents();
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new java.awt.Color(255, 255, 255));
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 300, Short.MAX_VALUE));
		pack();
	}

	public static void main(String args[]) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} 
		catch (Exception ex) {
			java.util.logging.Logger.getLogger(NauEspaial.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}       
		NauEspaial f = new NauEspaial();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Naus Espaials");
		f.setContentPane(new PanelNau());
		f.setSize(480, 560);
		f.setVisible(true);
	}
}



class PanelNau extends JPanel implements Runnable, KeyListener{   
	private int numNaus=3;    
	Nau[] nau;
	Nau nauPropia;
	public int disparosEliminados = 0;
	public boolean disparado = false;
	public Vector<disparo> vectorDisparos = new Vector<disparo>(0);
//	public Vector<Integer> vector = new Vector<>();
	public int contadorDisparos = 0;
	
	

	public PanelNau(){        
		nau = new Nau[numNaus];
		for (int i=0;i<nau.length;i++) {
			Random rand = new Random();
			int velocitat=(rand.nextInt(3)+5)*10;
			int posX=rand.nextInt(100)+30;
			int posY=rand.nextInt(100)+30;
			int dX=rand.nextInt(3)+1;
			int dY=rand.nextInt(3)+1;
			String nomNau = Integer.toString(i);
			nau[i]= new Nau(nomNau,posX,posY,dX,dY,velocitat);
		}

		// Creo la nau propia
		nauPropia = new Nau("NauNostra",200,400,10,0,100);

		// Creo fil per anar pintant cada 0,1 segons el joc per pantalla
		Thread n = new Thread(this);
		n.start();         

		// Creo listeners per a que el fil principal del programa gestioni
		// esdeveniments del teclat
		addKeyListener(this);
		setFocusable(true);               

	}

	
	
	public void run() {
		System.out.println("Inici fil repintar");
		while(true) {
			try { Thread.sleep(100);} catch(Exception e) {} // espero 0,1 segons
			//System.out.println("Repintant");
			repaint();            
		}                   
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(int i=0; i<nau.length;++i) {
			nau[i].pinta(g);
			nauPropia.pinta(g);
		}
		
		
		if (disparado) {
			
			for (int i = 0; i < vectorDisparos.size(); i++) {

					vectorDisparos.get(i).pinta(g);
					if (vectorDisparos.get(i).getY() < 0) {
						vectorDisparos.get(i).noSeguir();
						System.out.println(i);
						vectorDisparos.remove(i);
					}
			}
		}
	}

	// Metodes necesaris per gestionar esdeveniments del teclat
	@Override
	public void keyTyped(KeyEvent e) {
	}

//	new Vector();
//	
//	add (objecte)
//	
//	get(i)
//	
//	size ()
//	
//	remove(int posicio)
//	
//	remove(object)
	@Override
	public void keyPressed(KeyEvent e) {
		
		//System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
		if (e.getKeyCode()==37) { nauPropia.esquerra();} //System.out.println("a l'esquerra"); }
		if (e.getKeyCode()==39) { nauPropia.dreta(); } //System.out.println("a la dreta"); }           
		
		if (e.getKeyCode()==32) {
			vectorDisparos.add(nauPropia.Disparo(nauPropia.getX() + 28, nauPropia.getY(), 0, -10, 100));
			disparado = true;
//			vectorDisparos.set(contadorDisparos, disparo).start();
			//nauPropia.Disparo(nauPropia.x(), nauPropia.y(), 0, 10,100);
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}



class Nau extends Thread {
	private String nomNau;
	private int x,y;
	private int dsx,dsy,v;
	private int tx = 10;
	private int ty = 10;

	private String img = "/images/nau.jpg";
	private Image image;

	public Nau(String nomNau, int x, int y, int dsx, int dsy, int v ) {
		this.nomNau = nomNau;
		this.x=x;
		this.y=y;
		this.dsx=dsx;
		this.dsy=dsy;
		this.v=v;
		image = new ImageIcon(Nau.class.getResource("nau.png")).getImage();
		Thread t = new Thread(this); 
		t.start();
		
	}

	public disparo Disparo(int x, int y, int dsx, int dsy, int v) {

		disparo disparo = new disparo(x, y, dsx, dsy, v);
		disparo.start();
		
		return disparo;
	}

	public int velocitat (){ 
		return v;
	}

	public int getX () {
		return x;
	}

	public int getY () {
		return y;
	}

	public void moure (){
		x=x + dsx;
		y=y + dsy;
		// si arriva als marges ...
		if ( x>= 450 - tx || x<= tx) dsx = - dsx;
		if ( y >= 500 - ty || y<=ty ) dsy = - dsy;
	}

	public void pinta (Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(this.image, x, y, null);
	}


	public void run() {
		while (true) { 
			//System.out.println("Movent nau numero " + this.nomNau);
			try { Thread.sleep(this.v); } catch (Exception e) {}
			moure();
		}
	}

	public void esquerra() {
		this.dsx = -10; 
	}

	public void dreta() {
		this.dsx = 10; 
	}
}

class disparo extends Thread {
	private boolean seguir = true;
	private String nom;
	private int x,y;
	private int dsx,dsy,v;
	private int tx = 10;
	private int ty = 10;
	Thread y1;

	private String img = "/images/Disparo.jpg";
	private Image image;

	public disparo(int x, int y, int dsx, int dsy, int v ) {
		System.out.println("dispara");
		this.nom = nom;
		this.x=x;
		this.y=y;
		this.dsx=dsx;
		this.dsy=dsy;
		this.v=v;
		image = new ImageIcon(Nau.class.getResource("Disparo.png")).getImage();
		y1 = new Thread(this); 
		y1.start();
	}

	public int velocitat (){ 
		return v;
	}

	public int getX () {
		return x;
	}

	public int getY () {
		return y;
	}

	public void moure (){
		x=x + dsx;
		y=y + dsy;
		// si arriva als marges ...
		if ( x>= 450 - tx || x<= tx) {
			
		}
		if ( y >= 500 - ty || y<=ty ) {
			
		}
	}

	public void pinta (Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(this.image, x, y, null);
	}


	public void run() {
		while (seguir) { 
			//System.out.println("Movent nau numero " + this.nomNau);
			try { Thread.sleep(this.v); } catch (Exception e) {}
			moure();
			System.out.println("Movent " + y);
		}
	}
	
	public void noSeguir() {
		seguir = false;
		
	}
}
