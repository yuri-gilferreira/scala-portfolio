file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/project/Dependencies.scala
### file%3A%2F%2F%2FC%3A%2FUsers%2Fyurig%2FDocuments%2Fprojetos%2Fscala-portfolio%2Fbackend%2Fproject%2FDependencies.scala:6: error: unclosed string literal
    org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0" % Test
                                                      ^

occurred in the presentation compiler.

action parameters:
uri: file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/project/Dependencies.scala
text:
```scala
import sbt._

object Dependencies {
  lazy val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.2.13" % Test,
    org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0" % Test
    "com.holdenkarau" %% "spark-testing-base" % "2.4.3_0.14.0" % Test
  )

  val circeVersion = "0.13.0"
  val pureconfigVersion = "0.15.0"
  val sparkVersion = "3.2.1"

  lazy val core = Seq(

    // support for JSON formats
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-literal" % circeVersion,

    // support for typesafe configuration
    "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,

    // parallel collections
    "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
    
    // spark
    // "org.apache.spark" %% "spark-sql" % sparkVersion % Provided, // for submiting spark app as a job to cluster
    "org.apache.spark" %% "spark-sql" % sparkVersion, // for simple standalone spark app

     
    // spark - excluding slf4j-log4j12 to avoid SLF4J multiple bindings issue
    "org.apache.spark" %% "spark-sql" % sparkVersion exclude("org.slf4j", "slf4j-log4j12"),
    "com.lihaoyi" %% "requests" % "0.6.5", 
    
    // logging
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
}

```



#### Error stacktrace:

```
scala.meta.internal.tokenizers.Reporter.syntaxError(Reporter.scala:23)
	scala.meta.internal.tokenizers.Reporter.syntaxError$(Reporter.scala:23)
	scala.meta.internal.tokenizers.Reporter$$anon$1.syntaxError(Reporter.scala:33)
	scala.meta.internal.tokenizers.Reporter.syntaxError(Reporter.scala:25)
	scala.meta.internal.tokenizers.Reporter.syntaxError$(Reporter.scala:25)
	scala.meta.internal.tokenizers.Reporter$$anon$1.syntaxError(Reporter.scala:33)
	scala.meta.internal.tokenizers.LegacyScanner.getStringLit(LegacyScanner.scala:553)
	scala.meta.internal.tokenizers.LegacyScanner.fetchDoubleQuote$1(LegacyScanner.scala:372)
	scala.meta.internal.tokenizers.LegacyScanner.fetchToken(LegacyScanner.scala:376)
	scala.meta.internal.tokenizers.LegacyScanner.nextToken(LegacyScanner.scala:211)
	scala.meta.internal.tokenizers.LegacyScanner.foreach(LegacyScanner.scala:1011)
	scala.meta.internal.tokenizers.ScalametaTokenizer.uncachedTokenize(ScalametaTokenizer.scala:24)
	scala.meta.internal.tokenizers.ScalametaTokenizer.$anonfun$tokenize$1(ScalametaTokenizer.scala:17)
	scala.collection.concurrent.TrieMap.getOrElseUpdate(TrieMap.scala:962)
	scala.meta.internal.tokenizers.ScalametaTokenizer.tokenize(ScalametaTokenizer.scala:17)
	scala.meta.internal.tokenizers.ScalametaTokenizer$$anon$2.apply(ScalametaTokenizer.scala:332)
	scala.meta.tokenizers.Api$XtensionTokenizeDialectInput.tokenize(Api.scala:25)
	scala.meta.tokenizers.Api$XtensionTokenizeInputLike.tokenize(Api.scala:14)
	scala.meta.internal.parsers.ScannerTokens$.apply(ScannerTokens.scala:912)
	scala.meta.internal.parsers.ScalametaParser.<init>(ScalametaParser.scala:33)
	scala.meta.parsers.Parse$$anon$1.apply(Parse.scala:35)
	scala.meta.parsers.Api$XtensionParseDialectInput.parse(Api.scala:25)
	scala.meta.internal.semanticdb.scalac.ParseOps$XtensionCompilationUnitSource.toSource(ParseOps.scala:17)
	scala.meta.internal.semanticdb.scalac.TextDocumentOps$XtensionCompilationUnitDocument.toTextDocument(TextDocumentOps.scala:206)
	scala.meta.internal.pc.SemanticdbTextDocumentProvider.textDocument(SemanticdbTextDocumentProvider.scala:54)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticdbTextDocument$1(ScalaPresentationCompiler.scala:356)
```
#### Short summary: 

file%3A%2F%2F%2FC%3A%2FUsers%2Fyurig%2FDocuments%2Fprojetos%2Fscala-portfolio%2Fbackend%2Fproject%2FDependencies.scala:6: error: unclosed string literal
    org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0" % Test
                                                      ^