package main.sensor.response

data class BallsResponse(val data:List<Ball>): BaseResponse(null, null)
data class Ball(val x: Float, val y: Float){
    override fun toString(): String {
        return "X: $x, Y:$y"
    }
}
