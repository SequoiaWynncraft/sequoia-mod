package dev.lotnest.sequoia.feature;

import com.google.common.collect.ComparisonChain;
import dev.lotnest.sequoia.component.CoreComponent;

public abstract class Feature extends CoreComponent implements Comparable<Feature> {
    private CategoryType categoryType = CategoryType.UNCATEGORIZED;

    //    @Persisted(i18nKey = "sequoia.feature.userFeature.userEnabled")
    //    public final Config<Boolean> userEnabled = new Config<>(true);

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
        return this.getClass().getSimpleName().replace("Feature", "");
    }

    public void onEnable() {}

    public void onDisable() {}

    /**
     * Whether a feature is enabled
     */
    public final boolean isEnabled() {
        return true;
        //        return Managers.Feature.isEnabled(this);
    }

    public void setUserEnabled(boolean newState) {
        //        this.userEnabled.store(newState);
        tryUserToggle();
    }

    //    @Override
    //    public final void updateConfigOption(Config<?> config) {
    //        // if user toggle was changed, enable/disable feature accordingly
    //        if (config.getFieldName().equals("userEnabled")) {
    //            // Toggling before init does not do anything, so we don't worry about it for now
    //            tryUserToggle();
    //            return;
    //        }
    //
    //        // otherwise, trigger regular config update
    //        callOnConfigUpdate(config);
    //    }

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
                .compare(this.getCategory().toString(), other.getCategory().toString())
                .compare(this.getTranslatedName(), other.getTranslatedName())
                .result();
    }
}
