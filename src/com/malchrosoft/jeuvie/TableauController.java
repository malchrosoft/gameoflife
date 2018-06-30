/*
 * Copyright MalchroSoft - Aymeric MALCHROWICZ. All right reserved.
 * The source code that contains this comment is an intellectual property
 * of MalchroSoft [Aymeric MALCHROWICZ]. Use is subject to licence terms.
 */
package com.malchrosoft.jeuvie;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author malchrowicz
 */
public class TableauController implements Initializable
{
	@FXML
	private Canvas mainCanvas;

	@FXML
	private Button button;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb)
	{
		System.out.println(url);
		System.out.println(rb.toString());

	}

}
