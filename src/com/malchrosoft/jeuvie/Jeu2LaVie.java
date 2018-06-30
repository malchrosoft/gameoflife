/*
 * Copyright MalchroSoft - Aymeric MALCHROWICZ. All right reserved.
 * The source code that contains this comment is an intellectual property
 * of MalchroSoft [Aymeric MALCHROWICZ]. Use is subject to licence terms.
 */
package com.malchrosoft.jeuvie;

import com.malchrosoft.debug.Log;
import java.net.URL;
import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author malchrowicz
 */
public class Jeu2LaVie extends Application
{
	private final int width = 1024;
	private final int height = 768;
	private final int cellSize = 5;

	private Thread demoThread;

	@Override
	public void start(Stage primaryStage)
	{
		Canvas canvas = new Canvas(width, height);
		Simulation sim = new Simulation(canvas);
		sim.init(new AmasCellulaire((int) width / cellSize, (int) height / cellSize).generate(), Optional.empty());

//		GraphicsContext gc = canvas.getGraphicsContext2D();
//
//		gc.setLineWidth(2.0);
//		gc.setFill(Color.RED);
//		gc.strokeRoundRect(10, 10, 50, 50, 10, 10);
//		gc.fillRoundRect(100, 10, 50, 50, 10, 10);
//		gc.setFill(Color.BLUE);
//		gc.strokeOval(10, 70, 50, 30);
//		gc.fillOval(100, 70, 50, 30);
//		gc.strokeLine(200, 50, 300, 50);
//		gc.strokeArc(320, 10, 50, 50, 40, 80, ArcType.ROUND);
//		gc.fillArc(320, 70, 50, 50, 0, 120, ArcType.OPEN);
		URL mainPanelDesciptionFile = this.getClass().getResource("Tableau.fxml");
		Log.info("FXML url : " + mainPanelDesciptionFile.getPath());
//
//		Parent root = new FXMLLoader.load(mainPanelDesciptionFile);
//		System.out.println(root.getAccessibleText());
//
//
//		TableauController tbc = new TableauController();
//
//		primaryStage.setScene(tbc);
		StackPane root = new StackPane();
		root.setStyle("-fx-padding: 0;" +
			"-fx-border-style: solid inside;" +
			"-fx-border-width: 2;" +
			"-fx-border-insets: 2;" +
			"-fx-border-radius: 2;" +
			"-fx-border-color: black;");
		root.getChildren().add(canvas);

//		Button btn = new Button();
//		btn.setText("Say 'Hello World'");
//		btn.setOnAction(ev -> System.out.println("Hello World!"));
//		root.getChildren().add(btn);
		Scene scene = new Scene(root);

		primaryStage.setTitle("Canvas XP-rience - Aymeric MALCHROWICZ");
		primaryStage.setScene(scene);
		primaryStage.show();

		scene.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> sim.toggle());
		scene.addEventHandler(KeyEvent.KEY_PRESSED, e ->
		{
			if (e.getCode() != KeyCode.D)
			{
				stopDemo();
			}
			if (e.getCode() == KeyCode.C)
			{
				gliderGunMode(sim);
			}
			else if (e.getCode() == KeyCode.G)
			{
				newGenMode(sim);
			}
			else if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER)
			{
				sim.toggle();
			}
			else if (e.getCode() == KeyCode.PLUS || e.getCode() == KeyCode.UP || e.getCharacter().equals("+"))
			{
				sim.speedInc();
			}
			else if (e.getCode() == KeyCode.MINUS || e.getCode() == KeyCode.DOWN || e.getCharacter().equals("-"))
			{
				sim.speedDec();
			}
			else if (e.getCode() == KeyCode.D)
			{
				if (demoThread == null)
				{
					demoThread = new Thread(() -> demoMode(1, sim));
					demoThread.start();
				}
			}
		});
		primaryStage.setOnCloseRequest(w ->
		{
			sim.stop();
			stopDemo();
		});
	}

	private void stopDemo()
	{
		if (demoThread != null && !demoThread.isInterrupted())
		{
			demoThread.interrupt();
			demoThread = null;
		}
	}

	private void gliderGunMode(Simulation sim)
	{
		AmasCellulaire ac = new AmasCellulaire((int) width / cellSize, (int) height / cellSize);
		ac.generateCanon();
		sim.init(ac, Optional.of("Canon de Glider"));
	}

	private void newGenMode(Simulation sim)
	{
		AmasCellulaire ac = new AmasCellulaire((int) width / cellSize, (int) height / cellSize);
		ac.generate();
		sim.init(ac, Optional.of("Nouvelle génération cellulaire"));
	}

	private void demoMode(int step, Simulation sim)
	{
		switch (step)
		{
			case 0:
			case 1:
				sim.init(new AmasCellulaire((int) width / cellSize, (int) height / cellSize).generate(),
					Optional.of("Mode de démonstration"));
				sim.setSpeed(1);
				break;
			case 2:
				newGenMode(sim);
				sim.setSpeed(10);
				break;
			case 3:
				gliderGunMode(sim);
				sim.setSpeed(-10);
				break;
			case 4:
				sim.init(new AmasCellulaire((int) width / cellSize, (int) height / cellSize).generateCanon(),
					Optional.of("Vitesse maximale - Mode canon"));
				sim.setSpeed(20);
				break;
			case 5:
				sim.init(new AmasCellulaire((int) width / cellSize, (int) height / cellSize).generate(),
					Optional.of("Vitesse ralentie - Mode cellulaire normal"));
				sim.setSpeed(-30);
				break;
			default:
				return;
		}
		if (demoThread == null || demoThread.isInterrupted()) return;
		try
		{
			Thread.sleep(4000);
		} catch (InterruptedException ex)
		{
			//
		}
		sim.run();
		if (demoThread == null || demoThread.isInterrupted()) return;
		try
		{
			// 2 minutes
			Thread.sleep(2 * 60 * 1000);
		} catch (InterruptedException ex)
		{
			//
		}
		if (step < 5) step++;
		else step = 1;
		if (demoThread != null && !demoThread.isInterrupted()) demoMode(step, sim);
		else return;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		Log.get().setLevel(Log.Level.DEBUG);
		launch(args);
	}

}
