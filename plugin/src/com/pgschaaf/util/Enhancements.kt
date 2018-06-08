/*
 * Copyright 2018 P.G. Schaaf <paul.schaaf@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.pgschaaf.util

import com.intellij.openapi.vcs.IssueNavigationLink
import org.jetbrains.rpc.LOG
import java.util.*
import java.util.stream.Stream

// Click on the String value to test the plugin
private const val testPlugin = "wikipedia:Kotlin_(programming_language)"

@Suppress("UNCHECKED_CAST")
fun <T> Stream<T?>.withoutNulls() = filter {it != null} as Stream<T>

/** Return a Stream of the key-value pairs **/
fun ResourceBundle.keysAndValues(): Stream<Pair<String, String>> =
      this.keySet().stream().map {it to getString(it)}

fun <T> ClassLoader.tryToLoad(name: String) =
      try {
         @Suppress("UNCHECKED_CAST")
         Class.forName(name, true, this) as Class<T>
      }
      catch (e: ClassNotFoundException) {
         LOG.info("$this could not load class: '$name'")
         null
      }

/** Return a copy of this string with the outer layer of quotes removed, or this string if there are none. **/
val String.unquoted
   get() =
      if (this.isEmpty())
         this
      else when (first()) {
         '"'  -> removeSurrounding("\"")
         '\'' -> removeSurrounding("'")
         else -> this
      }

/** Return the URL to which this link will navigate. **/
fun IssueNavigationLink.destinationFor(text: String) = issuePattern.toRegex().replace(text, linkRegexp)
