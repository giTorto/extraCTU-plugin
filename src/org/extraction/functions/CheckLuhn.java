package org.extraction.functions;

import com.google.refine.expr.EvalError;
import com.google.refine.grel.ControlFunctionRegistry;
import com.google.refine.grel.Function;
import org.json.JSONException;
import org.json.JSONWriter;
import org.extraction.services.Operazione;
import java.util.Properties;

/**
 * Created by giulian on 02/05/14.
 */
public class CheckLuhn implements Function {
    @Override
    public Object call(Properties bindings, Object[] args) throws JSONException {
        if (args.length == 1) {
            Object stringa = args[0];
            int[] values;

            if (stringa == null)
                return "empty";

            if (stringa instanceof String){
                if (("".equals((String) stringa)) || (" ".equals((String) stringa))){
                    return "empty";
                }

                try{
                    values = Operazione.stringToIntArray((String)stringa);
                    return Operazione.checkIdentificationNumbers(values);
                }catch (Exception e){
                    return new EvalError(e.getMessage());
                }
            }
        }

        return new EvalError(ControlFunctionRegistry.getFunctionName(this) + "expects 1 string containing ONLY an identification numbers");
    }

    @Override
    public void write(JSONWriter writer, Properties options) throws JSONException {
        writer.object();
        writer.key("description");
        writer.value("Returns if the identification number respects the Luhn algorithm or not");
        writer.key("params");
        writer.value("1 string");
        writer.key("returns");
        writer.value("true or false");
        writer.endObject();
    }
}
