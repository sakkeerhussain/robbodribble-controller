package main.forms

import main.controllers.BallModel
import main.controllers.BotLocation
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class BallsUI : JPanel() {
    val BALL_WIDTH: Int = 15
    val BALL_HEIGHT: Int = 15
    private var balls: List<BallModel>
    private var bot: BotLocation?

    init {
        this.balls = ArrayList<BallModel>()
        this.bot = null
        this.isOpaque = false
    }

    private fun draw(g: Graphics) {
        val g2d = g as Graphics2D
        g2d.color = Color.GREEN
        for (i in 0..28) {
            g2d.fillRect(convertX(10f * i), convertY(0f), 1, convertBallY(180f))
        }
        for (i in 0..18) {
            g2d.fillRect(convertX(0f), convertY(10f * i), convertBallX(280f), 1)
        }
        g2d.color = Color.YELLOW
        for (ball in balls) {
            g2d.fillOval(convertBallX(ball.ball.center.x), convertBallY(ball.ball.center.y), BALL_WIDTH, BALL_HEIGHT)
        }
        if (bot != null) {
            g2d.color = Color.BLACK
            g2d.stroke = BasicStroke(5f);
            g2d.drawLine(convertX(bot!!.frontLeft.x), convertY(bot!!.frontLeft.y),
                    convertX(bot!!.frontRight.x), convertY(bot!!.frontRight.y))
            g2d.color = Color.BLUE
            g2d.drawLine(convertX(bot!!.backLeft.x), convertY(bot!!.backLeft.y),
                    convertX(bot!!.backRight.x), convertY(bot!!.backRight.y))
            g2d.drawLine(convertX(bot!!.frontLeft.x), convertY(bot!!.frontLeft.y),
                    convertX(bot!!.backLeft.x), convertY(bot!!.backLeft.y))
            g2d.drawLine(convertX(bot!!.frontRight.x), convertY(bot!!.frontRight.y),
                    convertX(bot!!.backRight.x), convertY(bot!!.backRight.y))
        }
    }

    override fun paint(g: Graphics) {
        this.draw(g)
    }

    private fun convertY(v: Float): Int {
        return convertBallY(v) + BALL_HEIGHT / 2
    }

    private fun convertX(v: Float): Int {
        return (convertBallX(v) + BALL_WIDTH / 2)
    }

    private fun convertBallY(v: Float): Int {
        return ((v * (this.height - BALL_HEIGHT) / 180).toInt())
    }

    private fun convertBallX(v: Float): Int {
        return (v * (this.width - BALL_WIDTH) / 280).toInt()
    }

    fun setBalls(balls: List<BallModel>){
        this.balls = balls
    }

    fun setBot(bot: BotLocation?){
        this.bot = bot
    }

}