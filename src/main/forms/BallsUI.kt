package main.forms

import main.controllers.BallModel
import main.controllers.BotLocation
import main.utils.Path
import main.utils.PathVertex
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class BallsUI : JPanel() {
    private val BALL_WIDTH: Int = 15
    private val BALL_HEIGHT: Int = 15
    var balls: List<BallModel> = ArrayList<BallModel>()
    var bot: BotLocation? = null
    var path: Path = Path()
        set(value) {
            val vertices = ArrayList<PathVertex>()
            value.vertices.filterTo(vertices) { it.point.x != -1f }
            field.vertices = vertices
        }

    init {
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
        if (path != null) {
            g2d.color = Color.ORANGE
            g2d.stroke = BasicStroke(5f);
            for (i in 1 until path!!.vertices.size) {
                val v1 = path!!.get(i - 1)
                val v2 = path!!.get(i)
                g2d.drawLine(convertX(v1.point.x), convertY(v1.point.y),
                        convertX(v2.point.x), convertY(v2.point.y))
            }
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
}