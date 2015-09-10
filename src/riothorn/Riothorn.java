package riothorn;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Riothorn {

    final ScriptEngine engine;
    final Invocable invocable;

    public Riothorn() {
        this(new ScriptEngineManager().getEngineByName("JavaScript"));
    }

    public Riothorn(ScriptEngine scriptEngine) {
        engine = scriptEngine;
        invocable = (Invocable) engine;
        try (Reader reader = new InputStreamReader(Riothorn.class.getResourceAsStream("/riothorn/riot-server.js"), "ASCII")) {
            // rhino's global() confuses the browserify prelude, remove it
            engine.eval("global = undefined;");

            engine.eval(reader);

            // invokeFunction doesn't seem to work with namespacing
            engine.eval("compile = riot.compile; render = riot.render; function renderJson(tag, json){return render(tag, JSON.parse(json));}");
        } catch (IOException | ScriptException e) {
            throw new RuntimeException("Failed to load riot-server.js", e);
        }
    }

    public Tag compile(String tagSource) {
        try {
            return new Tag((String)invocable.invokeFunction("compile", tagSource));
        } catch (ScriptException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public class Tag {
        final String code;
        final Object evaled;

        Tag(String code) throws ScriptException {
            this.code = code;
            evaled = engine.eval(code);
        }

        String renderJson(Object opts) {
            try {
                return (String)invocable.invokeFunction("renderJson", evaled, opts);
            } catch (ScriptException | NoSuchMethodException e) {
                throw new RuntimeException("riot rendering failed", e);
            }
        }

        public String toJavaScript() {
            return code;
        }
    }
}
