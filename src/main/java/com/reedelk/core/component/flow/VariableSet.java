package com.reedelk.core.component.flow;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.Serializable;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent(
        name = "Variable Set",
        description = "Sets a variable in the flow context with the provided value. " +
                "The assigned value must be serializable.")
@Component(service = VariableSet.class, scope = PROTOTYPE)
public class VariableSet implements ProcessorSync {

    @Reference
    private ScriptEngineService scriptEngine;

    @Hint("mySampleVariable")
    @Example("mySampleVariable")
    @Property("Name")
    @PropertyDescription("The name of the variable to be set in the flow context.")
    private String name;

    @MimeTypeCombo
    @InitValue(MimeType.MIME_TYPE_ANY)
    @DefaultRenameMe(MimeType.MIME_TYPE_ANY)
    @Example(MimeType.MIME_TYPE_APPLICATION_JSON)
    @Property("Mime type")
    @PropertyDescription("The mime type of the value this context variable will be bound to.")
    private String mimeType;

    @Hint("my variable content")
    @InitValue("#[message.payload()]")
    @Example("<code>message.attributes().pathParams</code>")
    @Property("Value")
    @PropertyDescription("The value to assign to the context variable being set. It might be a static or dynamic value.")
    private DynamicObject value;

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        if (StringUtils.isBlank(name)) {
            throw new ESBException("Variable name must not be empty");
        }

        MimeType mimeType = MimeType.parse(this.mimeType, MimeType.ANY);

        Serializable result = (Serializable) scriptEngine.evaluate(value, mimeType, flowContext, message).orElse(null);

        flowContext.put(name, result);

        return message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(DynamicObject value) {
        this.value = value;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
