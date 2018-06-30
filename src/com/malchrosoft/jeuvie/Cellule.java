/*
 * Copyright MalchroSoft - Aymeric MALCHROWICZ. All right reserved.
 * The source code that contains this comment is an intellectual property
 * of MalchroSoft [Aymeric MALCHROWICZ]. Use is subject to licence terms.
 */
package com.malchrosoft.jeuvie;

import java.util.Optional;

/**
 *
 * @author Aymeric Malchrowicz / MalchroSoft
 */
public class Cellule
{
	public enum State
	{
		ALIVE,
		DEAD,
		CREATING,
		DYING,
		INDETERMINATED
	}

	private State state;
	private State subState;
	private int x;
	private int y;

	public Cellule(int x, int y)
	{
		this(0, 0, Optional.empty());
	}

	public Cellule(int x, int y, Optional<State> initialState)
	{
		this.x = x;
		this.y = y;
		state = State.INDETERMINATED;
		initialState.ifPresent(is -> this.state = is);
	}

	public State state()
	{
		return state;
	}

	public State getSubState()
	{
		return subState;
	}

	public void evolve()
	{
		if (this.subState == State.DYING) kill();
		else if (this.subState == State.CREATING) create();
	}

	public final void kill()
	{
		if (this.state == State.ALIVE)
			this.subState = State.DYING;
		else
			this.subState = State.DEAD;

		this.state = State.DEAD;
	}

	public final void create()
	{
		if (this.state == State.DEAD)
			this.subState = State.CREATING;
		else this.subState = State.ALIVE;

		this.state = State.ALIVE;
	}

	public int x()
	{
		return x;
	}

	public int y()
	{
		return y;
	}

}
