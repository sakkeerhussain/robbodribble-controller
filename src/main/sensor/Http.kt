package main.sensor

class Http {
    companion object {
        fun calibrateRef(point: Int) {
            ApiService.Factory.create().calibrateRef(point)
                    .subscribe({ result ->
                        System.out.println("Result ==> ${result.status}-${result.message}")
                    }, { error ->
                        System.out.println("Result ==> ${error.localizedMessage}")
                    })
        }
    }
}