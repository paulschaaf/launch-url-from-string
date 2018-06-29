package com.pgschaaf.launchurlfromstring.clickableStringEnhancement

import com.intellij.psi.impl.FakePsiElement
import org.junit.Test

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
class PsiElement: AbstractPsiElement() {
   override fun makeElementWithString(string: String?) = object: FakePsiElement() {
      override fun getText() = string
      override fun getParent() = this
   }

   @Test fun `surrounding double quotes are stripped`() =
         "\"hello\"" yields "hello"

   @Test fun `surrounding single quotes are stripped`() =
         "'hello'" yields "hello"

   @Test fun `empty Quotes are ignored`() =
         "''" yields null
}
