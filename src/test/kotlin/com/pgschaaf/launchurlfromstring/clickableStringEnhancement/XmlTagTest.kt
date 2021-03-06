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

package com.pgschaaf.launchurlfromstring.clickableStringEnhancement

import com.intellij.psi.impl.source.xml.XmlTagImpl
import com.intellij.psi.xml.XmlTagValue
import org.junit.Test

class XmlTagTest: AbstractPsiElementTest() {
   override fun makeElementWithString(string: String) = object: XmlTagImpl() {
      override fun getValue() = FakeXmlTag(string)
   }

   @Test fun `surrounding double quotes are not stripped`() =
         "\"hello\"" yields "\"hello\""

   @Test fun `surrounding single quotes are not stripped`() =
         "'hello'" yields "'hello'"

   private class FakeXmlTag(private val str: String?): XmlTagValue {
      override fun getTrimmedText() = str ?: ""

      override fun hasCDATA() = false

      override fun getChildren() = throw IllegalAccessError("This method should not have been called!")

      override fun getTextRange() = throw IllegalAccessError("This method should not have been called!")

      override fun setEscapedText(p0: String?) = throw IllegalAccessError("This method should not have been called!")

      override fun getTextElements() = throw IllegalAccessError("This method should not have been called!")

      override fun setText(p0: String?) = throw IllegalAccessError("This method should not have been called!")

      override fun getText() = throw IllegalAccessError("This method should not have been called!")
   }
}
