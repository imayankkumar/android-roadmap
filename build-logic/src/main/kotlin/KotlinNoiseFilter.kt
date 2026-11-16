import java.io.FilterReader
import java.io.Reader
import java.io.StringReader

/**
 * Removes Kotlin's @Metadata(...) and @SourceDebugExtension(...) blocks, and their imports,
 * from decompiled Java. Used by the `decompile` task.
 */
class KotlinNoiseFilter(input: Reader) : FilterReader(StringReader(clean(input.readText()))) {

    private companion object {
        val noisyImports = Regex("""^import kotlin\.(Metadata|jvm\.internal\.SourceDebugExtension);\n""", RegexOption.MULTILINE)

        fun clean(source: String): String {
            var text = source.replace(noisyImports, "")
            for (annotation in listOf("@Metadata(", "@SourceDebugExtension(")) {
                text = removeAnnotation(text, annotation)
            }
            return text
        }

        /** Drops each `annotation(...)` with its arguments and the line break after it. */
        fun removeAnnotation(text: String, annotation: String): String {
            val out = StringBuilder()
            var i = 0
            while (true) {
                val start = text.indexOf(annotation, i)
                if (start < 0) break
                out.append(text, i, start)
                var depth = 0
                var j = start + annotation.length - 1
                var inString = false
                while (j < text.length) {
                    val c = text[j]
                    if (inString) {
                        if (c == '\\') j++ else if (c == '"') inString = false
                    } else when (c) {
                        '"' -> inString = true
                        '(' -> depth++
                        ')' -> { depth--; if (depth == 0) break }
                    }
                    j++
                }
                i = j + 1
                if (i < text.length && text[i] == '\n') i++
            }
            out.append(text, i, text.length)
            return out.toString()
        }
    }
}
