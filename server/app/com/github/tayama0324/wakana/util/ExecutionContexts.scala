package com.github.tayama0324.wakana.util

import scala.concurrent.ExecutionContext

trait UsesExecutionContext {
  implicit def executionContext: ExecutionContext
}

trait MixInDefaultExecutionContext {
  // TODO: Create good execution context.
  implicit val executionContext = ExecutionContext.global
}
