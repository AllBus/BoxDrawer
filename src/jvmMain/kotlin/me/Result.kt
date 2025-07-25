package me

sealed interface Result<out T> {

    fun <S> flatMap(body: (T) -> Result<S>): Result<S>
    fun <S> map(body: (T) -> S): Result<S>
    fun <S> map(body: (T) -> S, errorBody: (Throwable) -> S): Result<S>
   // fun ifError(errorBody: (Throwable) -> Result<T>): Result<T>
    fun get():T
    fun isError(): Boolean
    fun isSuccess():Boolean


    class Success<out T>(
        val data: T
    ) : Result<T> {
        override fun <S> flatMap(body: (T) -> Result<S>): Result<S> = body.invoke(data)
        override fun <S> map(body: (T) -> S): Result<S> = Result.Success(body.invoke(data))
      //  override fun <S> ifError(errorBody: (Throwable) -> Result<T>): Result<T> = this

        override fun get(): T = data
        override fun isError(): Boolean = false
        override fun isSuccess(): Boolean = true
        override fun <S> map(body: (T) -> S, errorBody: (Throwable) -> S): Result<S> = Result.Success(body.invoke(data))

    }

    class Error<T>(val error: Throwable) : Result<T> {
        override fun <S> flatMap(body: (T) -> Result<S>): Result<S> = Error(this.error)
        override fun <S> map(body: (T) -> S): Result<S> = Error(this.error)
        override fun get(): T = throw  error
        override fun isError(): Boolean = true
        override fun isSuccess(): Boolean = false
        override fun <S> map(body: (T) -> S, errorBody: (Throwable) -> S): Result<S> = Result.Success(errorBody.invoke(error))
    }
}


fun check() {
    val a = Result.Success("Abc")

    a.map { s ->
        if (s == "DEF")
            Result.Success(456)
        else
            Result.Error(NullPointerException(""))
    }
}
