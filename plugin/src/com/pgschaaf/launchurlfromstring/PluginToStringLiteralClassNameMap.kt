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

package com.pgschaaf.launchurlfromstring

/**
 * This is a map of plugin ID's to the name of the class that implements the parse node for a literal
 * String in that plugin. If a language is not listed here it will only work if the plugin uses the
 * standard Java classes (which seems unlikely).
 */
// I implemented this an 'object::get(String): String' rather than a 'fun f(): Map<String, String>()
// because this makes the mapping code far easier to read and manage.
object PluginToStringLiteralClassNameMap {
   operator fun get(key: String) = when (key) {
      "Dart"                 -> "com.jetbrains.lang.dart.psi.DartStringLiteralExpression"
      "Gosu"                 -> "gw.gosu.ij.psi.GosuPsiLiteralExpression"
      "JavaScript"           -> "com.intellij.lang.javascript.psi.JSLiteralExpression"
      "org.jetbrains.kotlin" -> "org.jetbrains.kotlin.psi.KtStringTemplateExpression"
      "com.jetbrains.php"    -> "com.jetbrains.php.lang.psi.elements.StringLiteralExpression"
      "PythonCore"           -> "com.jetbrains.python.psi.PyStringLiteralExpression"
//      "org.jetbrains.plugins.ruby" -> "org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral"
//      "org.jetbrains.plugins.ruby" -> "org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RLiteral"
      else                   -> null
   }
}
