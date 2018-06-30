/*
 * Copyright MalchroSoft - Aymeric MALCHROWICZ. All right reserved.
 * The source code that contains this comment is an intellectual property
 * of MalchroSoft [Aymeric MALCHROWICZ]. Use is subject to licence terms.
 */
package com.malchrosoft.jeuvie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Aymeric Malchrowicz / MalchroSoft
 */
public class AmasCellulaire
{
	private final List<Cellule> cells;
	private int width;
	private int height;

	public AmasCellulaire(int width, int height)
	{
		this.width = width;
		this.height = height;
		cells = new ArrayList<>();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
			{
				cells.add(new Cellule(x, y, Optional.of(Cellule.State.INDETERMINATED)));
			}
	}

	public AmasCellulaire generate()
	{
		cells.stream()
			.forEach(c
				->
			{
				if (Math.random() > 0.5) c.create();
				else c.kill();
			});
		return this;
	}

	public AmasCellulaire generateCanon()
	{
		List<Cellule> canonCells = Arrays.asList(
			ccc(0, 4), ccc(1, 4),
			ccc(0, 5), ccc(1, 5),
			ccc(10, 4), ccc(10, 5), ccc(10, 6),
			ccc(11, 3), ccc(11, 7),
			ccc(12, 2), ccc(12, 8),
			ccc(13, 2), ccc(13, 8), ccc(13, 8),
			ccc(14, 5),
			ccc(15, 3), ccc(15, 7),
			ccc(16, 4), ccc(16, 5), ccc(16, 6),
			ccc(17, 5),
			ccc(20, 2), ccc(20, 3), ccc(20, 4),
			ccc(21, 2), ccc(21, 3), ccc(21, 4),
			ccc(22, 1), ccc(22, 5),
			ccc(24, 0), ccc(24, 1), ccc(24, 5), ccc(24, 6),
			ccc(34, 2), ccc(34, 3),
			ccc(35, 2), ccc(35, 3)
		);

		cells.stream()
			.forEach(c ->
			{
				if (canonCells.stream()
					.filter(cc -> cc.x() == c.x() && cc.y() == c.y())
					.findFirst()
					.isPresent())
				{
					c.create();
				}
				else
					c.kill();
			});
		return this;
	}

	/**
	 * Quicly create an alive cell at position
	 * @param x
	 * @param y
	 * @return the created cell
	 */
	private static Cellule ccc(int x, int y)
	{
		return new Cellule(x, y, Optional.of(Cellule.State.ALIVE));
	}

	public List<Cellule> getCells()
	{
		return cells;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

}
