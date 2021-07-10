package net.blancworks.multis.rendering;

import net.blancworks.api.rendering.models.BWModel;
import net.minecraft.client.render.model.json.ModelTransformation;

public abstract class MultisModel extends BWModel{

    public abstract ModelTransformation getTransformation();
    public abstract boolean isSideLit();

}
