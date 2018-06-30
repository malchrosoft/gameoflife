/*
 * Copyright MalchroSoft - Aymeric MALCHROWICZ. All right reserved.
 * The source code that contains this comment is an intellectual property
 * of MalchroSoft [Aymeric MALCHROWICZ]. Use is subject to licence terms.
 */
package com.malchrosoft.jeuvie;

import com.malchrosoft.debug.Log;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Aymeric Malchrowicz / MalchroSoft
 */
public class Simulation
{
	private final Canvas c;
	private boolean running;
	private boolean initialized;
	private Thread loopThread;

	private int speedFactor = 1;

	private int inc;

	private AmasCellulaire ac;

	public Simulation(Canvas c)
	{
		this.c = c;
		this.running = false;
		this.initialized = false;
		this.inc = 0;
	}

	public final void init(AmasCellulaire ac, Optional<String> comment)
	{
		this.initialized = false;
		stop();

		boolean isNewAC = this.ac != ac;
		Platform.runLater(() ->
		{
			GraphicsContext gc = c.getGraphicsContext2D();

			if (isNewAC && this.ac != null)
			{
				gc.setFill(Color.BLACK);
				gc.fillRect(0, 0, c.getWidth(), c.getHeight());
			}

			gc.setFill(Color.WHITESMOKE);
			gc.setFont(Font.font("ARIAL", 16));
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);

			if (isNewAC)
			{
				gc.fillText("Click/Press space screen to start the new simulation...", c.getWidth() / 2, c.getHeight() /
					2);
			}
			else
				gc.fillText("Click/Press space on screen to resume simulation...", c.getWidth() / 2, c.getHeight() / 2);

			gc.setFill(Color.AQUA);
			gc.setFont(Font.font("ARIAL", FontWeight.BOLD, FontPosture.ITALIC, 20));
			comment.ifPresent(s -> gc.fillText("...:: " + s.toUpperCase() + " ::...", c.getWidth() / 2, c.getHeight() /
				2 + 26));
		});
		this.ac = ac;

		// At the end
		this.initialized = true;
	}

	public final void run()
	{
		Log.debug("invoke run method");
		if (!initialized)
		{
			Log.error("Simulation is nor initialized");
			return;
		}
		if (running)
		{
			Log.warn("already running");
			return;
		}
		this.loopThread = new Thread(() -> loop());
		this.running = true;
		loopThread.start();
	}

	public final void stop()
	{
		Log.debug("invoke stop method");
		if (!running)
		{
			Log.warn("already stopped");
			return;
		}
		this.running = false;
		loopThread.interrupt();
		init(ac, Optional.empty());
	}

	private void loop()
	{
		// init loop constants

		GraphicsContext gc = c.getGraphicsContext2D();
		c.setCache(false);
		c.setCursor(Cursor.CROSSHAIR);
		gc.setTextAlign(TextAlignment.LEFT);
		gc.setTextBaseline(VPos.BASELINE);
		while (running)
		{
			// Update game
			long aliveCount = updateStates();

			Platform.runLater(() ->
			{
				// Draw scene
				gc.setFill(Color.BLACK);
				gc.fillRect(0, 0, c.getWidth(), c.getHeight());
//				gc.setFill(Color.CYAN);
//				gc.setFont(Font.font("ARIAL", FontWeight.BOLD, 12));
//				gc.fillText("Occurence : " + inc + " - running ? " + isRunning(), 10, c.getHeight() - 10);

				// Draw middle objects
				drawCells(gc);

				gc.setFill(Color.WHITESMOKE);
				gc.setFont(Font.font("ARIAL", FontWeight.BOLD, 12));
				gc.setTextAlign(TextAlignment.LEFT);
				gc.fillText("Occurence : " + inc + " => " + aliveCount + " cellules vivantes", 10, c
					.getHeight() - 10);
				gc.setTextAlign(TextAlignment.RIGHT);
				gc.fillText("Vitesse : " + speedFactor, c.getWidth() - 10, c.getHeight() - 10);
			});

			inc++;

//			Thread.yield();
			try
			{
				Thread.sleep(110 - speedFactor * 5);
			} catch (InterruptedException ex)
			{
				Log.info(ex.getLocalizedMessage());
			}
		}
	}

	public void speedInc()
	{
		if (speedFactor < 20)
			speedFactor++;
	}

	public void speedDec()
	{
		if (speedFactor > -50)
			speedFactor--;
	}

	public void setSpeed(int speedF)
	{
		this.speedFactor = Math.min(20, Math.max(speedF, -50));
	}

	private long updateStates()
	{
		Cellule.State[][] states = new Cellule.State[ac.getWidth()][ac.getHeight()];
		ac.getCells().stream()
			.forEach(cell ->
			{
				cell.evolve();
				states[cell.x()][cell.y()] = cell.state();
			});

		// Apply rules
		ac.getCells().stream()
			.forEach(cell
				->
			{
				int x = cell.x();
				int y = cell.y();

				int xG = x - 1 < 0 ? ac.getWidth() - 1 : x - 1;
				int xD = x + 1 >= ac.getWidth() ? 0 : x + 1;
				int yH = y - 1 < 0 ? ac.getHeight() - 1 : y - 1;
				int yB = y + 1 >= ac.getHeight() ? 0 : y + 1;

				int voisinesAliveCpt = 0;

				if (states[xG][yH] == Cellule.State.ALIVE) voisinesAliveCpt++;
				if (states[x][yH] == Cellule.State.ALIVE) voisinesAliveCpt++;
				if (states[xD][yH] == Cellule.State.ALIVE) voisinesAliveCpt++;
				if (states[xG][y] == Cellule.State.ALIVE) voisinesAliveCpt++;
				if (states[xD][y] == Cellule.State.ALIVE) voisinesAliveCpt++;
				if (states[xG][yB] == Cellule.State.ALIVE) voisinesAliveCpt++;
				if (states[x][yB] == Cellule.State.ALIVE) voisinesAliveCpt++;
				if (states[xD][yB] == Cellule.State.ALIVE) voisinesAliveCpt++;

				// Change state ?
				if (cell.state() == Cellule.State.ALIVE)
				{
					// Doit-elle mourir
					if (voisinesAliveCpt < 2 || voisinesAliveCpt > 3)
					{
						cell.kill();
					}
				}
				else if (cell.state() == Cellule.State.DEAD)
				{
					// Doit-elle naitre
					if (voisinesAliveCpt == 3)
					{
						cell.create();
					}
				}
			});
		return ac.getCells().stream()
			.filter(c -> c.state() == Cellule.State.ALIVE)
			.count();
	}

	private void drawCells(GraphicsContext gc)
	{
		gc.setStroke(Color.RED);
		ac.getCells().stream()
			.filter(cell -> cell.getSubState() == Cellule.State.DYING)
			.forEach(c -> gc.strokeRect(c.x() * 5 + 2, c.y() * 5 + 2, 1, 1));

		gc.setFill(Color.PINK);
		gc.setStroke(Color.HOTPINK);
		ac.getCells().stream()
			.filter(cell -> cell.getSubState() == Cellule.State.CREATING)
			.forEach(c ->
			{
				gc.strokeOval(c.x() * 5, c.y() * 5, 5, 5);
				gc.fillOval(c.x() * 5, c.y() * 5, 5, 5);
			});

//		gc.setFill(Color.LIGHTBLUE);
//		gc.setStroke(Color.CORNFLOWERBLUE);
		gc.setFill(Color.LAWNGREEN);
		gc.setStroke(Color.GREEN);
		ac.getCells().stream()
			.filter(cell -> cell.getSubState() == Cellule.State.ALIVE)
			.forEach(c ->
			{
				gc.fillOval(c.x() * 5, c.y() * 5, 5, 5);
				gc.strokeOval(c.x() * 5, c.y() * 5, 5, 5);
			});

	}

	public boolean isRunning()
	{
		return running;
	}

	public final void toggle()
	{
		if (running) stop();
		else run();
	}

}
