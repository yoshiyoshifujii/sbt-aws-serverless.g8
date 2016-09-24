package com.example

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import spray.json._

class Hello extends RequestStreamHandler {
  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    output.write(JsObject(
      "message" -> JsString("World!!")
    ).compactPrint.getBytes("utf-8"))
  }
}
