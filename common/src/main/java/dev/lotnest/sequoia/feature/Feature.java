package dev.lotnest.sequoia.feature;

import com.google.common.collect.ComparisonChain;
import dev.lotnest.sequoia.component.CoreComponent;
import dev.lotnest.sequoia.manager.Managers;

public abstract class Feature extends CoreComponent implements Comparable<Feature> {
    private CategoryType categoryType = CategoryType.UNCATEGORIZED;

    @Override
    public String getTypeName() {
        return "Feature";
    }

    public CategoryType getCategory() {
        return categoryType;
    }

    public void setCategory(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public String getTranslatedDescription() {
        return getTranslation("description");
    }

    public String getShortName() {
        return getClass().getSimpleName().replace("Feature", "");
    }

    public void onEnable() {}

    public void onDisable() {}

    /**
     * Whether a feature is enabled
     */
    public final boolean isEnabled() {
        return Managers.Feature.isEnabled(this);
    }

    public void setUserEnabled(boolean newState) {
        tryUserToggle();
    }

    /**
     * Updates the feature's enabled/disabled state to match the user's setting, if necessary
     */
    private void tryUserToggle() {
        //        if (userEnabled.get()) {
        //            Managers.Feature.enableFeature(this);
        //        } else {
        //            Managers.Feature.disableFeature(this);
        //        }
    }

    @Override
    public int compareTo(Feature other) {
        return ComparisonChain.start()
                .compare(getCategory().toString(), other.getCategory().toString())
                .compare(getTranslatedName(), other.getTranslatedName())
                .result();
    }
}
