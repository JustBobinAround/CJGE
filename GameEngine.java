import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public abstract class GameEngine implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener, Runnable {
//+================================================================================+
//| System Window Constants                                                        |
//+================================================================================+
	public static final int SYSTEM_SCREEN_WIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice().getDisplayMode().getWidth();
	public static final int SYSTEM_SCREEN_HEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice().getDisplayMode().getHeight();

//+================================================================================+
//| Renderer Variables                                                             |
//+================================================================================+	
	public float scale = 1f;
	private BufferedImage image;
	private Canvas canvas;
	private BufferStrategy canvasBS;
	private Graphics g;
	private int[] pixels;

//+================================================================================+
//| Variables for Game Update                                                      |
//+================================================================================+
	private Thread thread;
	private boolean running = false;
	public double UPDATE_CAP = 1.0 / 60.0;

//+================================================================================+
//| Engine Constructor                                                             |
//+================================================================================+	
	public GameEngine() {
		JFrame frame = new JFrame();
		image = new BufferedImage(SYSTEM_SCREEN_WIDTH, SYSTEM_SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
		canvas = new Canvas();
		int canvasW = SYSTEM_SCREEN_WIDTH;
		canvasW = (int) (canvasW * scale);
		int canvasH = SYSTEM_SCREEN_HEIGHT;
		canvasH = (int) (canvasH * scale);
		Dimension canvasDimension = new Dimension(canvasW, canvasH);
		canvas.setPreferredSize(canvasDimension);
		canvas.setMinimumSize(canvasDimension);
		canvas.setMaximumSize(canvasDimension);

		frame.setPreferredSize(canvasDimension);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.add(canvas, BorderLayout.CENTER);

		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		pixels = ((DataBufferInt) this.getImage().getRaster().getDataBuffer()).getData();
		frame.setVisible(true);
		canvas.requestFocus();
		canvas.createBufferStrategy(2);
		canvasBS = canvas.getBufferStrategy();
		g = canvasBS.getDrawGraphics();
		this.start();
	}

//+================================================================================+
//| LISTENER IMPLEMENTATIONS                                                       |
//+================================================================================+	
	private final int NUMBER_OF_KEYS =256;
	private boolean[] keys = new boolean[NUMBER_OF_KEYS];
	private boolean[] priorKeys = new boolean[NUMBER_OF_KEYS];
	
	private final int NUMBER_OF_MOUSE_BUTTONS = 5;
	private boolean[] mouseButtons = new boolean[this.NUMBER_OF_MOUSE_BUTTONS];
	private boolean[] priorMouseButtons = new boolean[this.NUMBER_OF_MOUSE_BUTTONS];
	
	public int mouseX=0;
	public int mouseY=0;
	public int scroll=0;
	
	//keyUpdate method is called in gameUpdate run() 
	//prior to abstract preRenderOperations method
	private void keyUpdate() {
		for(int ii =0; ii < this.NUMBER_OF_KEYS;ii++) {
			priorKeys[ii] = keys[ii];
		}
		for(int ii =0; ii < this.NUMBER_OF_MOUSE_BUTTONS;ii++) {
			priorMouseButtons[ii] = mouseButtons[ii];
		}
		
	}
	
	public boolean isKey(int keyCode) {
		return keys[keyCode];
	}
	public boolean isKeyUp(int keyCode) {
		return !keys[keyCode] && priorKeys[keyCode];
	}
	public boolean isKeyDown(int keyCode) {
		return keys[keyCode] && !priorKeys[keyCode];
	}
	public boolean isMouseButton(int buttonCode) {
		return mouseButtons[buttonCode];
	}
	public boolean isMouseUp(int buttonCode) {
		return !mouseButtons[buttonCode] && priorMouseButtons[buttonCode];
	}
	public boolean isMouseButtonsDown(int buttonCode) {
		return mouseButtons[buttonCode] && !priorMouseButtons[buttonCode];
	}
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()]=true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()]=false;
	}

	
	//Don't need these
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	
	
	
	public void mousePressed(MouseEvent e) {
		mouseButtons[e.getButton()] = true;
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButtons[e.getButton()] = false;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = (int)(e.getX() / this.scale);
		mouseY = (int)(e.getY() / this.scale);		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = (int)(e.getX() / this.scale);
		mouseY = (int)(e.getY() / this.scale);
		
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scroll = e.getWheelRotation();
	}	
//+================================================================================+
//| GAME CLOCK/UPDATE                                                              |
//+================================================================================+	
	BufferedImage img = null;

	// Pre-game Operations
	public void start() {
		preGameOperations();
		thread = new Thread(this);
		thread.run();
	}
	
	// PREGAME OPERATIONS START
	public abstract void preGameOperations() ;
	// PREGAME OPERATIONS END
//+================================================================================+
//| THE IMPORTANT RENDER STUFF                                                     |
//+================================================================================+	
	// GAME UPDATE START
	public abstract void preRenderUpdate();
	// GAME UPDATE END

	// GAME RENDER START
	public abstract void renderUpdate();
	// GAME RENDER END
	

	// Main Game Thread
	@Override
	public void run() {
		running = true;
		boolean render = false;
		double firstTime = 0;
		double lastTime = System.nanoTime() / 1000000000.0;
		double deltaTime = 0;
		double unprocessedTime = 0;

		double frameTime = 0;
		double frames = 0;
		double fps = 0;

		while (running) {
			firstTime = System.nanoTime() / 1000000000.0;
			deltaTime = firstTime - lastTime;
			lastTime = firstTime;
			unprocessedTime += deltaTime;
			frameTime += deltaTime;
			while (unprocessedTime >= UPDATE_CAP) {
				unprocessedTime -= UPDATE_CAP;
				render = true;
				// GAME UPDATE START
				keyUpdate();
				preRenderUpdate();
				// GAME UPDATE END
				if (frameTime >= 1.0) {
					frameTime = 0;
					fps = frames;
					frames = 0;
					System.out.println("FPS: " + fps);
				}
			}

			if (render) {
				this.clear();
				// RENDER UPDATE START
				renderUpdate();
				// RENDER UPDATE END
				this.update();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		stop();
		dispose();
	}
	
	// FINAL OPERATIONS START
	public abstract void stop() ;
	// FINAL OPERATIONS END

	//CLOSE Operations
	private void dispose() {
		//Not needed at the moment
	}


//+================================================================================+
//| RENDERING METHODS                                                              |
//+================================================================================+	
	public int INVISIBLE = new Color(145, 132, 176).getRGB();

	public void update() {
		g.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
		canvasBS.show();
	}

	public void clear() {
		for (int ii = 0; ii < pixels.length; ii++) {
			pixels[ii] = 0xff000000;
		}
	}
	//TODO: add scaling, rotation and possibly skewing? methods
	public void drawBufferedImage(BufferedImage bi, int x, int y) {
		for(int jj = 0; jj < bi.getHeight(); jj++) {
			for(int ii = 0; ii < bi.getWidth(); ii++) {
				if(bi.getRGB(ii, jj)==INVISIBLE) {
					//DRAW NOTHING
				}else if((jj+y)<0 || (jj+y)>SYSTEM_SCREEN_HEIGHT-1 || (ii+x)<0 || (ii+x)>SYSTEM_SCREEN_WIDTH-1){
					//DRAW NOTHING
				}else{
					pixels[((jj+y)*SYSTEM_SCREEN_WIDTH)+(x+ii)] = bi.getRGB(ii, jj);
				}
			}
		}
	}
	//This method is included for people who wanted to write there own image class.
	//I wanted an image class that was serializable, so I made this standard rgb input that should work fine
	//for most peoples needs
	public void drawRGBSequence(int[][] rgb,int x, int y) {
		for(int jj = 0; jj < rgb.length; jj++) {
			for(int ii = 0; ii < rgb[jj].length; ii++) {
				if(rgb[jj][ii]==INVISIBLE) {
					//DRAW NOTHING
				}else if((jj+y)<0 || (jj+y)>SYSTEM_SCREEN_HEIGHT-1 || (ii+x)<0 || (ii+x)>SYSTEM_SCREEN_WIDTH-1){
					//DRAW NOTHING
				}else{
					pixels[((jj+y)*SYSTEM_SCREEN_WIDTH)+(x+ii)] = rgb[jj][ii];
				}
			}
		}
	}
//+================================================================================+
//| GETTERS AND SETTERS                                                            |
//+================================================================================+	
	private BufferedImage getImage() {
		return image;
	}

}
