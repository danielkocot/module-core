package com.reedelk.core.component.resource;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.resource.ResourceText;
import org.osgi.service.component.annotations.Component;
import org.reactivestreams.Publisher;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ESBComponent("Resource Read Text")
@Component(service = ResourceReadText.class, scope = PROTOTYPE)
public class ResourceReadText extends ResourceReadComponent implements ProcessorSync {

    @Property("Resource file")
    @PropertyInfo("The path and name of the file to be read from the project's resources folder.")
    private ResourceText resourceFile;

    @Property("Auto mime type")
    @Default("true")
    @PropertyInfo("If true, the mime type of the payload is determined from the extension of the resource read.")
    private boolean autoMimeType;

    @Property("Mime type")
    @MimeTypeCombo
    @Default(MimeType.MIME_TYPE_TEXT_PLAIN)
    @When(propertyName = "autoMimeType", propertyValue = "false")
    @When(propertyName = "autoMimeType", propertyValue = When.BLANK)
    @PropertyInfo("The mime type of the resource read from local project's resources directory.")
    private String mimeType;

    @Override
    public Message apply(Message message, FlowContext flowContext) {

        Publisher<String> data = resourceFile.data();

        String resourceFilePath = resourceFile.path();

        MessageAttributes attributes = createAttributes(ResourceReadText.class, resourceFilePath);

        MimeType mimeType = mimeTypeFrom(autoMimeType, this.mimeType, resourceFilePath);

        return MessageBuilder.get()
                .attributes(attributes)
                .withText(data)
                .mimeType(mimeType)
                .build();
    }

    public void setResourceFile(ResourceText resourceFile) {
        this.resourceFile = resourceFile;
    }

    public void setAutoMimeType(boolean autoMimeType) {
        this.autoMimeType = autoMimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
