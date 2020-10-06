package com.ustadmobile.core.util.ext


fun String.hexStringToByteArray() = this.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

fun String.inBrackets() = "($this)"

expect fun String.base64StringToByteArray(): ByteArray

fun String?.toQueryLikeParam() = if(this.isNullOrEmpty()) "%" else "%$this%"

fun String?.alternative(alternative: String) = if(this.isNullOrEmpty()) alternative else this
