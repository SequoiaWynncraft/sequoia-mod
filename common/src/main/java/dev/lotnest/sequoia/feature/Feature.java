package dev.lotnest.sequoia.feature;

import com.google.common.collect.ComparisonChain;
import dev.lotnest.sequoia.component.CoreComponent;
import dev.lotnest.sequoia.manager.Managers;

public abstract class Feature extends CoreComponent implements Comparable<Feature> {
    @Override
    public String getTypeName() {
        return "Feature";
    }

    public String getTranslatedDescription() {
        return getTranslation("description");
    }

    public String getShortName() {
        return getClass().getSimpleName().replace("Feature", "");
    }

    public void onEnable() {}

    public void onDisable() {}

    public final boolean isEnabled() {
        return Managers.Feature.isEnabled(this);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            Managers.Feature.enableFeature(this);
        } else {
            Managers.Feature.disableFeature(this);
        }
    }

    @Override
    public int compareTo(Feature other) {
        return ComparisonChain.start()
                .compare(getTranslatedName(), other.getTranslatedName())
                .result();
    }

    public boolean isHidden() {
        return false;
    }

    public boolean isExperimental() {
        return false;
    }
}
