/* Copyright 2017, Emmanouil Antonios Platanios. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.platanios.tensorflow.api.ops

import org.platanios.tensorflow.api._

import org.scalatest._

/**
  * @author Emmanouil Antonios Platanios
  */
class VariableSpec extends FlatSpec with Matchers {
  "Variable creation" must "work" in {
    val graph = tf.Graph()
    val variable = tf.createWith(graph = graph) {
      val initializer = tf.constantInitializer(Tensor(Tensor(2, 3)))
      tf.variable("variable", INT64, Shape(1, 2), initializer)
    }
    assert(variable.dataType === INT64)
    assert(graph.getCollection(tf.Graph.Keys.GLOBAL_VARIABLES).contains(variable))
    assert(graph.getCollection(tf.Graph.Keys.TRAINABLE_VARIABLES).contains(variable))
    val session = tf.Session(graph = graph)
    session.run(targets = variable.initializer)
    val outputs = session.run(fetches = variable.value)
    val expectedResult = Tensor(INT64, Tensor(2, 3))
    assert(outputs(0, 0).scalar === expectedResult(0, 0).scalar)
    assert(outputs(0, 1).scalar === expectedResult(0, 1).scalar)
    session.close()
    graph.close()
  }

  "Variable assignment" must "work" in {
    val graph = tf.Graph()
    val (variable, variableAssignment) = tf.createWith(graph = graph) {
      val a = tf.constant(Tensor(Tensor(5, 7)), INT64, name = "A")
      val initializer = tf.constantInitializer(Tensor(Tensor(2, 3)))
      val variable = tf.variable("variable", INT64, Shape(1, 2), initializer)
      val variableAssignment = variable.assign(a)
      (variable, variableAssignment)
    }
    assert(variable.dataType === INT64)
    assert(graph.getCollection(tf.Graph.Keys.GLOBAL_VARIABLES).contains(variable))
    assert(graph.getCollection(tf.Graph.Keys.TRAINABLE_VARIABLES).contains(variable))
    val session = tf.Session(graph = graph)
    session.run(targets = variable.initializer)
    val outputs = session.run(fetches = Seq(variableAssignment, variable.value))
    assert(outputs.length == 2)
    val expectedResult = Tensor(INT64, Tensor(5, 7))
    assert(outputs(1)(0, 0).scalar === expectedResult(0, 0).scalar)
    assert(outputs(1)(0, 1).scalar === expectedResult(0, 1).scalar)
    session.close()
    graph.close()
  }
}
