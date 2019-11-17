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
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
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
		} catch (Exception ex) {
			java.util.logging.Logger.getLogger(NauEspaial.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
		NauEspaial f = new NauEspaial();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Naus Espaials");
		f.setContentPane(new PanelNau());
		f.setSize(480, 560);
		f.setVisible(true);
	}
}

class PanelNau extends JPanel implements Runnable, KeyListener {

	// Declarem un int per a comprobar si totes les naus estan destruides
	public int nausDestruides = 0;
	private int numNaus = 10;
	Nau[] nau;
	Nau nauPropia;
	// Declarem un boolean per a comprobar si sha disparat
	public boolean disparado = false;
	// Declarem un vector en el que guardarem els trets aixi es podra disparar
	// infinitament
	public Vector<disparo> vectorDisparos = new Vector<disparo>(0);

	public PanelNau() {
		nau = new Nau[numNaus];
		for (int i = 0; i < nau.length; i++) {
			Random rand = new Random();
			int velocitat = (rand.nextInt(3) + 5) * 10;
			int posX = rand.nextInt(100) + 30;
			int posY = rand.nextInt(100) + 30;
			int dX = rand.nextInt(3) + 1;
			int dY = rand.nextInt(3) + 1;
			String nomNau = Integer.toString(i);
			nau[i] = new Nau(nomNau, posX, posY, dX, dY, velocitat, false);
		}

		// Creo la nau propia
		nauPropia = new Nau("NauNostra", 200, 400, 10, 0, 100, false);

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
		while (true) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			} // espero 0,1 segons
			// System.out.println("Repintant");
			repaint();

		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Si la nauPropia no esta destruida i encara no estan destruides totes les naus
		// enemigues el joc continua
		if (nauPropia.destruida == false && nausDestruides != numNaus) {

			for (int i = 0; i < nau.length; ++i) {

				// Si la nauPropia colisiona amb una altra nau la nau Propia es destrueix
				if ((nauPropia.getY() > (nau[i].getY()) && nauPropia.getY() < (nau[i].getY() + 100))
						&& (nauPropia.getX() > (nau[i].getX())) && nauPropia.getX() < (nau[i].getX() + 60)
						&& nau[i].destruida == false) {
					nauPropia.noSeguir();
					nauPropia.destruida = true;

					// Si no esta destruida pintem la nauPropia
				} else if (nauPropia.destruida == false) {
					nauPropia.pinta(g);
				}

				// Si no esta destruida pintem les naus enemigues
				if (nau[i].destruida == false) {
					nau[i].pinta(g);
				}

				if (disparado) {

					for (int y = 0; y < vectorDisparos.size(); y++) {

						// System.out.println("NAU: x = " + nau[i].getX() + ", y = " + nau[i].getY());
						// System.out.println("DISPARO: x = " + vectorDisparos.get(y).getX() + ", y = "
						// + vectorDisparos.get(y).getY());

						vectorDisparos.get(y).pinta(g);

						// Si colisiona el tret amb una nau enemiga
						if ((vectorDisparos.get(y).getY() > (nau[i].getY())
								&& vectorDisparos.get(y).getY() < (nau[i].getY() + 100))
								&& (vectorDisparos.get(y).getX() > (nau[i].getX()))
								&& vectorDisparos.get(y).getX() < (nau[i].getX() + 60) && nau[i].destruida == false) {

							// Destruim la nau enemiga i el tret que ha colisionat
							nau[i].noSeguir();
							nau[i].destruida = true;
							// augmentem el contador de naus destruides
							nausDestruides++;
							// System.out.println("destruit");
							vectorDisparos.get(y).noSeguir();
							vectorDisparos.remove(y);

							// Si el tret surt de la pantalla es destrueix
						} else if (vectorDisparos.get(y).getY() < 0) {
							vectorDisparos.get(y).noSeguir();
							vectorDisparos.remove(y);
						}
					}
				}
			}

			// Sino es para el joc
		} else {
			System.exit(0);

		}
	}

	// Metodes necesaris per gestionar esdeveniments del teclat
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {

		// System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" +
		// e.getKeyChar());
		if (e.getKeyCode() == 37) {
			nauPropia.esquerra();
		} // System.out.println("a l'esquerra"); }
		if (e.getKeyCode() == 39) {
			nauPropia.dreta();
		} // System.out.println("a la dreta"); }

		// Si presionem el espai disparem
		if (e.getKeyCode() == 32) {
			vectorDisparos.add(nauPropia.Disparo(nauPropia.getX() + 28, nauPropia.getY(), 0, -10, 100));
			// Declarem la variable disparado a true per saber si sha disparat
			disparado = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}

class Nau extends Thread {
	// Afegim el boolean seguir per a detendre el metode run
	private boolean seguir = true;
	private String nomNau;
	private int x, y;
	private int dsx, dsy, v;
	private int tx = 10;
	private int ty = 10;
	// Afegim el boolean destruida per saber si la nau esta destruida
	public boolean destruida;

	private Image image;

	public Nau(String nomNau, int x, int y, int dsx, int dsy, int v, boolean destruida) {
		this.nomNau = nomNau;
		this.x = x;
		this.y = y;
		this.dsx = dsx;
		this.dsy = dsy;
		this.v = v;
		this.destruida = destruida;
		image = new ImageIcon(Nau.class.getResource("nau.png")).getImage();
		Thread t = new Thread(this);
		t.start();

	}

	// Afegim el metode disparo per a que cada cop es presioni l'espai es generi un
	// objecte disparo
	public disparo Disparo(int x, int y, int dsx, int dsy, int v) {

		disparo disparo = new disparo(x, y, dsx, dsy, v);
		disparo.start();

		return disparo;
	}

	public int velocitat() {
		return v;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void moure() {
		x = x + dsx;
		y = y + dsy;
		// si arriva als marges ...
		if (x >= 450 - tx || x <= tx)
			dsx = -dsx;
		if (y >= 500 - ty || y <= ty)
			dsy = -dsy;
	}

	public void pinta(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(this.image, x, y, null);
	}

	// Si no seguir es false no el fil deixa de funcionar
	public void run() {
		while (seguir) {
			// System.out.println("Movent nau numero " + this.nomNau);
			try {
				Thread.sleep(this.v);
			} catch (Exception e) {
			}
			moure();
		}
	}

	// Metode per finalitzar el run
	public void noSeguir() {
		seguir = false;

	}

	public void esquerra() {
		this.dsx = -10;
	}

	public void dreta() {
		this.dsx = 10;
	}
}

// Creem la clase disparo que es practicament igual que la clase nau
class disparo extends Thread {
	// Afegim el boolean seguir per a detendre el metode run
	private boolean seguir = true;
	private String nom;
	private int x, y;
	private int dsx, dsy, v;
	private int tx = 10;
	private int ty = 10;
	Thread y1;

	private Image image;

	public disparo(int x, int y, int dsx, int dsy, int v) {
		System.out.println("dispara");
		this.x = x;
		this.y = y;
		this.dsx = dsx;
		this.dsy = dsy;
		this.v = v;
		image = new ImageIcon(Nau.class.getResource("Disparo.png")).getImage();
		y1 = new Thread(this);
		y1.start();
	}

	public int velocitat() {
		return v;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void moure() {
		x = x + dsx;
		y = y + dsy;
		// si arriva als marges ...
		if (x >= 450 - tx || x <= tx) {

		}
		if (y >= 500 - ty || y <= ty) {

		}
	}

	public void pinta(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(this.image, x, y, null);
	}

	// Si no seguir es false no el fil deixa de funcionar
	public void run() {
		while (seguir) {
			// System.out.println("Movent nau numero " + this.nomNau);
			try {
				Thread.sleep(this.v);
			} catch (Exception e) {
			}
			moure();
			// System.out.println("Movent " + y);
		}
	}

	// Metode per parar el metode run
	public void noSeguir() {
		seguir = false;

	}
}
