package main.forms

import main.sensor.response.Ball
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import javax.swing.JPanel

class BallsUI(private val balls: List<Ball>) : JPanel() {

    init {
        this.isOpaque = false;
    }

    val BALL_WIDTH: Int = 15
    val BALL_HEIGHT: Int = 15

    private fun draw(g: Graphics) {
        val g2d = g as Graphics2D
        g2d.color = Color.YELLOW
        for (ball in balls) {
            g2d.fillOval(convertX(ball.x), convertY(ball.y), BALL_WIDTH, BALL_HEIGHT)
        }
        g2d.color = Color.WHITE
        for (i in 0..18){
            g2d.fillRect(convertX(10*i), convertY(0), 1, convertY(280))
        }
        for (i in 0..28){
            g2d.fillRect(convertX(0), convertY(10*i), convertX(180), 1)
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g);
        this.draw(g)
    }

    private fun convertY(y: Int): Int {
        return (y * (this.height - BALL_HEIGHT) / 280) + BALL_HEIGHT/2
    }

    private fun convertX(x: Int): Int {
        return (x * (this.width - BALL_WIDTH) / 180) + BALL_WIDTH/2
    }

}