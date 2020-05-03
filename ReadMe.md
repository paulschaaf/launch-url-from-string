# Launch URL from a String
**IntelliJ Plugin**

## Summary
Enables hyperlink navigation from any matching string literal, comment, XML attribute value, or XML element text to a URL defined by the "Issue Navigation" rules in the VCS settings.

## Background
Out of the box IntelliJ lets you define Regular Expressions to turn plain-text entries in your check-in comments and branch names into hyperlinks. The most common use case is to link issues back to your issue tracker. For instance the pattern and mapping shown here 
 
![IntelliJ Issue Navigation Settings](src/main/resources/IJIssueNavigation.png)

has turned the branch name ("DEVCCPERF-142") into a link to that case in a Jira installation.

## This Plugin
This plugin applies those same rules to literal strings, comments, XML attributes and XML element text. Now code like 
`@TestCase("DEVCCPERF-142")` or `<foo>DEVCCPERF-142</foo>` becomes hyperlinks, too.

Of course you can define link patterns to go anywhere you like: for instance you could link `[Ww]ikipedia:(.*)` to `http://en.wikipedia.org/w/index.php?title=Special:Search&search=$1`

### Supported Languages
- [Dart](https://www.dartlang.org/)
- [Gosu](https://gosu-lang.github.io/)
- [Java](https://www.java.com/en/)
- [JavaScript](https://www.javascript.com/)
- [Kotlin](https://kotlinlang.org/)
- [Scala](https://www.scala-lang.org/)
- [Php](http://www.php.net/)
- [Python](https://www.python.org/)
- [XML](https://www.w3.org/XML/) (attributes and element text)

### Limitations
Because the Regex's are matched against individual elements in the PSI parse tree, expressions like `"DEVCCPERF" + "-142"` can't be matched by a single pattern.

Also, it will only work with languages whose Strings Classes are explicitly listed in the file `StringLiteralClassNames.properties`.

 
# Installation Instructions
Install it like a normal plugin, then restart. Define a few Regex mappings under File/Settings/Version Control/Issue Navigation. Now you should be able to CTRL-click on matching Strings.
 
# Acknowledgements
Special thanks to Max Ishchenko, whose plugin [idea-navigate-url-from-literal](https://github.com/ishchenko/idea-navigate-from-literal) inspired and guided this one.

Thanks too to JetBrains for releasing [the source](https://github.com/JetBrains/intellij-community) to the wonderful [IntelliJ Community Edition](https://www.jetbrains.com/idea/features/), and for creating [Kotlin](https://kotlinlang.org/)&mdash;my new favorite programming language.

# Source Code
This plugin is written in Kotlin. The source is available on [GitHub](https://github.com/paulschaaf/launch-url-from-string) under the Apache License.
 