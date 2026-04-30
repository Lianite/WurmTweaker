package org.gotti.wurmtweaker.json;

public interface ContentHandler<T> {

    /** Matches the subdirectory name under data/, e.g. "skills". */
    String getTypeName();

    /** Gson target class for deserialization. */
    Class<T> getDefinitionClass();

    /** Called once per successfully parsed definition. */
    void apply(T definition);
}
