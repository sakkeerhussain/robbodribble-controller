package main.forms

import main.sensor.response.Ball
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import javax.swing.JPanel

class BallsUI : JPanel() {
    val BALL_WIDTH: Int = 15
    val BALL_HEIGHT: Int = 15
    private var balls: List<Ball>

    init {
        this.balls = ArrayList<Ball>()
        this.isOpaque = false;
    }

    private fun draw(g: Graphics) {
        val g2d = g as Graphics2D
        g2d.color = Color.GREEN
        for (i in 0..18) {
            g2d.fillRect(convertX(10f * i), convertY(0f), 1, convertBallY(280f))
        }
        for (i in 0..28) {
            g2d.fillRect(convertX(0f), convertY(10f * i), convertBallX(180f), 1)
        }
        g2d.color = Color.YELLOW
        for (ball in balls) {
            g2d.fillOval(convertBallX(ball.x), convertBallY(ball.y), BALL_WIDTH, BALL_HEIGHT)
        }
    }

    override fun paint(g: Graphics) {
        this.draw(g)
    }

    private fun convertY(y: Float): Int {
        return convertBallY(y) + BALL_HEIGHT / 2
    }

    private fun convertX(x: Float): Int {
        return (convertBallX(x) + BALL_WIDTH / 2)
    }

    private fun convertBallY(y: Float): Int {
        return ((y * (this.height - BALL_HEIGHT) / 280).toInt())
    }

    private fun convertBallX(x: Float): Int {
        return (x * (this.width - BALL_WIDTH) / 180).toInt()
    }

    fun setBalls(balls: List<Ball>){
        this.balls = balls
    }

}