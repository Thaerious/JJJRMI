package ca.frar.jjjrmi.translator;
import org.json.JSONObject;

final class EncodedReference extends JSONObject{
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("Encoder");
    EncodedReference(String ref){
        LOGGER.trace("new EncodedReference(" + ref + ")");
        this.put(Constants.PointerParam, ref);
    }
}