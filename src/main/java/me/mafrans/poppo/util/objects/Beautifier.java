package me.mafrans.poppo.util.objects;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.FileUtils;
import org.w3c.dom.Document;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

public class Beautifier {
    public static class Javascript {

        private final ScriptEngine engine;

        public Javascript() throws ScriptException, IOException {
            engine = new ScriptEngineManager().getEngineByName("nashorn");

            // this is needed to make self invoking function modules work
            // otherwise you won't be able to invoke your function
            engine.eval("var global = this;");
            engine.eval(FileUtils.readStream(getClass().getClassLoader().getResourceAsStream("js-beautify/js/lib/beautify.js")));
        }

        public String beautify(String javascriptCode) throws ScriptException, NoSuchMethodException {
            return (String) ((Invocable) engine).invokeFunction("js_beautify", javascriptCode);
        }
    }


    public static class CSS {

        private final ScriptEngine engine;

        public CSS() throws ScriptException, IOException {
            engine = new ScriptEngineManager().getEngineByName("nashorn");

            // this is needed to make self invoking function modules work
            // otherwise you won't be able to invoke your function
            engine.eval("var global = this;");
            engine.eval(FileUtils.readStream(getClass().getClassLoader().getResourceAsStream("js-beautify/js/lib/beautify-css.js")));
        }

        public String beautify(String javascriptCode) throws ScriptException, NoSuchMethodException {
            return (String) ((Invocable) engine).invokeFunction("css_beautify", javascriptCode);
        }
    }

    public static class HTML {

        private final ScriptEngine engine;

        public HTML() throws ScriptException, IOException {
            engine = new ScriptEngineManager().getEngineByName("nashorn");

            // this is needed to make self invoking function modules work
            // otherwise you won't be able to invoke your function
            engine.eval("var global = this;");
            engine.eval(FileUtils.readStream(getClass().getClassLoader().getResourceAsStream("js-beautify/js/lib/beautify-html.js")));
        }

        public String beautify(String javascriptCode) throws ScriptException, NoSuchMethodException {
            return (String) ((Invocable) engine).invokeFunction("html_beautify", javascriptCode);
        }
    }

    public static class XML {
        public String beautify(String string) throws TransformerException {
            Source xmlInput = new StreamSource(new StringReader(string));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        }
    }
}
