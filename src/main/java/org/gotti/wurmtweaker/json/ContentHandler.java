package org.gotti.wurmtweaker.json;

import java.io.File;

public interface ContentHandler<T> {

    /** Matches the subdirectory name under data/, e.g. "skills". */
    String getTypeName();

    /** Gson target class for deserialization. */
    Class<T> getDefinitionClass();

    /** Called once per successfully parsed definition. */
    void apply(T definition);

    /** Called with the source file; default delegates to apply(definition). */
    default void apply(T definition, File sourceFile) {
        apply(definition);
    }
}
