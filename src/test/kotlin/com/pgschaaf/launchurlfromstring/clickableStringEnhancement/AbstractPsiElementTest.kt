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

import com.intellij.psi.PsiElement
import com.pgschaaf.launchurlfromstring.clickableString
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

abstract class AbstractPsiElementTest {
   protected abstract fun makeElementWithString(string: String): PsiElement

   infix fun String?.yields(actual: String?) {
       if (actual == null) {
            assertFalse(makeElementWithString(this ?: "").clickableString.isPresent)
       }
       else if (actual.isBlank()) {
            assertFalse(makeElementWithString(this ?: "").clickableString.isPresent)
       }
       else {
           assertEquals(actual, makeElementWithString(this!!).clickableString.get())
       }
   }

   @Test fun `plain string is preserved`() =
         "hello" yields "hello"

   @Test fun `leading double quote is preserved`() =
         "\"hello" yields "\"hello"

   @Test fun `trailing single quote is preserved`() =
         "hello'" yields "hello'"

   @Test fun `empty String is ignored`() =
         ""  yields null

   @Test fun `null String is ignored`() =
         null yields null
}
