file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/src/test/scala/SimulationsTest.scala.scala
### java.lang.IndexOutOfBoundsException: 7

occurred in the presentation compiler.

action parameters:
offset: 0
uri: file:///C:/Users/yurig/Documents/projetos/scala-portfolio/backend/src/test/scala/SimulationsTest.scala.scala
text:
```scala
@@

```



#### Error stacktrace:

```
scala.reflect.internal.util.BatchSourceFile.offsetToLine(SourceFile.scala:201)
	scala.meta.internal.pc.MetalsGlobal$XtensionPositionMetals.toPos(MetalsGlobal.scala:668)
	scala.meta.internal.pc.MetalsGlobal$XtensionPositionMetals.toLsp(MetalsGlobal.scala:681)
	scala.meta.internal.pc.PcDocumentHighlightProvider.collect(PcDocumentHighlightProvider.scala:20)
	scala.meta.internal.pc.PcDocumentHighlightProvider.collect(PcDocumentHighlightProvider.scala:8)
	scala.meta.internal.pc.PcCollector.scala$meta$internal$pc$PcCollector$$collect$1(PcCollector.scala:292)
	scala.meta.internal.pc.PcCollector.traverseWithParent$1(PcCollector.scala:337)
	scala.meta.internal.pc.PcCollector.traverseSought(PcCollector.scala:472)
	scala.meta.internal.pc.PcCollector.resultWithSought(PcCollector.scala:266)
	scala.meta.internal.pc.PcCollector.result(PcCollector.scala:207)
	scala.meta.internal.pc.PcDocumentHighlightProvider.highlights(PcDocumentHighlightProvider.scala:29)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$documentHighlight$1(ScalaPresentationCompiler.scala:340)
```
#### Short summary: 

java.lang.IndexOutOfBoundsException: 7